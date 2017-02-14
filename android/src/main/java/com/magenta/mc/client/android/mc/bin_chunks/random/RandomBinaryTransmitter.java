package com.magenta.mc.client.android.mc.bin_chunks.random;

import com.magenta.mc.client.android.mc.bin_chunks.BinaryChunkResendable;
import com.magenta.mc.client.android.mc.bin_chunks.BinaryTransmissionTask;
import com.magenta.mc.client.android.mc.client.XMPPClient;
import com.magenta.mc.client.android.mc.client.resend.Resendable;
import com.magenta.mc.client.android.mc.client.resend.ResendableIdGenerator;
import com.magenta.mc.client.android.mc.client.resend.ResendableMetadata;
import com.magenta.mc.client.android.mc.client.resend.ResendableMgmt;
import com.magenta.mc.client.android.mc.client.resend.Resender;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.mc.storage.BinaryStorable;
import com.magenta.mc.client.android.mc.storage.Storable;
import com.magenta.mc.client.android.mc.storage.Storage;
import com.magenta.mc.client.android.rpc.xmpp.extensions.rpc.DefaultRpcResponseHandler;

import net.sf.microlog.core.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;
import EDU.oswego.cs.dl.util.concurrent.Mutex;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import EDU.oswego.cs.dl.util.concurrent.ThreadFactory;

public class RandomBinaryTransmitter {
    private static Mutex initLock = new Mutex();
    private static boolean initialized;

    // a thread pool with unbounded queue and limited number of threads
    // is going be used for scanning of binaries
    private static PooledExecutor threadPool;

    private static List listeners = new ArrayList();
    private static Mutex taskProcessMutex = new Mutex();
    private static Map runningTasks = new HashMap();
    public static ResendableMgmt BIN_TRANS_MGMT = new ResendableMgmt() {
        public boolean send(Resendable target) {
            RandomBinTransTask task = (RandomBinTransTask) target;
            if (XMPPClient.getInstance().isLoggedIn()) {
                new RandomBinaryTransmitter().process(task);
                return true;
            } else {
                return false;
            }
        }

        public void sent(ResendableMetadata metadata, String id, Object[] params) {
            if (params != null && params.length == 2) {
                try {
                    String uri = (String) params[0];
                    boolean lastChunk = ((Boolean) params[1]).booleanValue();
                    RandomBinaryTransmitter.chunkDelivered(id, uri, lastChunk);
                } catch (Exception e) {
                    LoggerFactory.getLogger(RandomBinaryTransmitter.class).error(e);
                }
            }
        }
    };

    public RandomBinaryTransmitter() {
        initThreadPool();
    }

    public static void addTransmitListener(RandomTransmitterListener listener) {
        listeners.add(listener);
    }

    public static void removeTransmitListener(RandomTransmitterListener listener) {
        for (Iterator it = listeners.iterator(); it.hasNext(); ) {
            if (it.next().equals(listener)) {
                it.remove();
                break;
            }
        }
    }

    private static void fireListeners(RandomTransmitterListener.RandomTransmitEvent event) {
        for (Iterator it = listeners.iterator(); it.hasNext(); ) {
            RandomTransmitterListener next = (RandomTransmitterListener) it.next();
            next.onEvent(event);
        }
    }

    public static void shutdown() {
        if (initialized && threadPool != null) {
            threadPool.shutdownNow();
            threadPool = null;
            initialized = false;
        }
    }

    private static boolean processTask(RandomBinTransTask task) {
        if (RandomBinTransTask.STATE_RECEIVED == task.getState()) {
            if (task.getCount() == 0) {
                BinaryStorable binary = Setup.get().getStorage().getBinary(task.getLocalUri());
                int chunkLength = Setup.get().getSettings().getIntProperty("bin.chunk.length", "50000");
                long blobLength = binary.length();
                int chunkCount = (int) (blobLength / chunkLength) + (blobLength % chunkLength > 0 ? 1 : 0);
                task.setChunkLength(chunkLength);
                task.setCount(chunkCount);
                Setup.get().getStorage().save(task);
                fireListeners(new RandomTransmitterListener.RandomTransmitEvent(task.getLocalUri(),
                        task.getCount(), task.getCurrentChunk()));
            }
            new RandomBinaryTransmitter().signalTaskDetails(task);
            return true;
        } else if (RandomBinTransTask.STATE_SIGNALLED == task.getState()) {
            String taskUri = task.getUri();
            if (acquireTaskRun(taskUri)) {
                try {
                    Storage storage = Setup.get().getStorage();
                    BinaryStorable binary = storage.getBinary(task.getLocalUri());
                    if (task.getCount() == 0) {
                        MCLoggerFactory.getLogger(RandomBinaryTransmitter.class).debug("Empty binary signalled, removing transmission: " + taskUri);
                        if (task.isDeleteBlob()) {
                            Setup.get().getStorage().delete(binary);
                        }
                        storage.delete(RandomBinTransTask.RESENDABLE_METADATA, RandomBinTransTask.escapeUri(taskUri));
                        fireListeners(new RandomTransmitterListener.RandomTransmitEvent(task.getLocalUri(),
                                task.getCount(), task.getCurrentChunk(),
                                RandomTransmitterListener.RandomTransmitEvent.STATUS_FILE_NOT_FOUND));
                        return false;
                    }
                    byte[] buffer = new byte[task.getChunkLength()];
                    int read = 0;
                    try {
                        read = binary.read(task.getPosition(), buffer, 0, task.getChunkLength());
                        if (read < 0) { // which designates the end of file
                            MCLoggerFactory.getLogger(RandomBinaryTransmitter.class).warn("Unexpected and of binary, removing transmission: " + task.getUri());
                            if (task.isDeleteBlob()) {
                                Setup.get().getStorage().delete(binary);
                            }
                            storage.delete(RandomBinTransTask.RESENDABLE_METADATA, RandomBinTransTask.escapeUri(taskUri));
                            fireListeners(new RandomTransmitterListener.RandomTransmitEvent(task.getLocalUri(),
                                    task.getCount(), task.getCurrentChunk(),
                                    RandomTransmitterListener.RandomTransmitEvent.STATUS_ENEXPECTED_END_OF_FILE));
                        }
                    } catch (IOException e) {
                        MCLoggerFactory.getLogger(RandomBinaryTransmitter.class).error("Bin chunk reading failed: " + task.getUri(), e);
                        new RandomBinaryTransmitter().handleScanningError(e, task);
                    }
                    if (read > 0) {
                        byte[] chunkData = new byte[read];
                        System.arraycopy(buffer, 0, chunkData, 0, read);
                        BinaryChunkResendable nextChunk = new BinaryChunkResendable();
                        int chunkOrder = task.getCurrentChunk();
                        if (task.getCurrentChunkId() == null) { // which means this is a new chunk
                            task.setReadWhileLastProcess(read);
                            task.setCurrentChunkId(Long.toString(ResendableIdGenerator.generateId()));
                        }
                        nextChunk.setId(task.getCurrentChunkId());
                        nextChunk.setData(chunkData);
                        nextChunk.setUri(taskUri);
                        nextChunk.setOrderNumber(chunkOrder);
                        Setup.get().getStorage().save(task);
                        DefaultRpcResponseHandler.binChunk(nextChunk);
                    }
                } finally {
                    releaseTask(taskUri);
                }
            }

            return true;
        } else {
            MCLoggerFactory.getLogger(RandomBinaryTransmitter.class).debug("Skip r.bin chunk (trans. not signalled)" + task.getUri());
            return false;
        }
    }

    public static boolean transmit(String uri, String localUri, boolean deleteBlobUponTransmission) {
        RandomBinTransTask transmissionTask = new RandomBinTransTask(uri, localUri, deleteBlobUponTransmission);
        Storage storage = Setup.get().getStorage();
        fireListeners(new RandomTransmitterListener.RandomTransmitEvent(transmissionTask.getLocalUri(),
                transmissionTask.getCount(), transmissionTask.getCurrentChunk()));
        Storable prevTask = storage.load(BinaryTransmissionTask.STORABLE_METADATA, transmissionTask.getId());
        if (prevTask != null) {
            return false;
        } else {
            Resender.getInstance().send(transmissionTask);
        }
        return true;
    }

    private static boolean acquireTaskRun(String url) {
        try {
            taskProcessMutex.acquire();
            if (runningTasks.containsKey(url)) {
                MCLoggerFactory.getLogger(RandomBinaryTransmitter.class).trace("binary scanning in progress, permission denied: " + url);
                return false;
            } else {
                runningTasks.put(url, Boolean.TRUE);
                MCLoggerFactory.getLogger(RandomBinaryTransmitter.class).trace("binary scanning permission granted: " + url);
                return true;
            }
        } catch (InterruptedException e) {
            MCLoggerFactory.getLogger(RandomBinaryTransmitter.class).trace("acquireTaskRun interrupted, skipping");
            return false;
        } finally {
            taskProcessMutex.release();
        }
    }

    private static boolean releaseTask(String url) {
        try {
            taskProcessMutex.acquire();
            return runningTasks.remove(url) != null;
        } catch (InterruptedException e) {
            return false;
        } finally {
            MCLoggerFactory.getLogger(RandomBinaryTransmitter.class).trace("binary scanning permission released: " + url);
            taskProcessMutex.release();
        }
    }

    public static void transDetailsReceived(String url) {
        Storage storage = Setup.get().getStorage();
        RandomBinTransTask task = (RandomBinTransTask) storage.load(RandomBinTransTask.RESENDABLE_METADATA, BinaryTransmissionTask.escapeUri(url));
        if (task == null) {
            MCLoggerFactory.getLogger(RandomBinaryTransmitter.class).warn("Signalled random binary task not found upon confirmation: " + url);
            return;
        }
        task.setState(BinaryTransmissionTask.STATE_SIGNALLED);
        storage.save(task);
        MCLoggerFactory.getLogger(RandomBinaryTransmitter.class).debug("Random binary task signalled: " + url);
        Resender.getInstance().resetRetransmissionTimeout(RandomBinTransTask.RESENDABLE_METADATA);
        Resender.getInstance().retransmit(RandomBinTransTask.RESENDABLE_METADATA);
    }

    private static void chunkDelivered(String chunkId, String uri, boolean lastChunk) {
        if (acquireTaskRun(uri)) {
            try {
                Storage storage = Setup.get().getStorage();
                RandomBinTransTask task = (RandomBinTransTask) storage.load(RandomBinTransTask.RESENDABLE_METADATA, RandomBinTransTask.escapeUri(uri));
                int status = RandomTransmitterListener.RandomTransmitEvent.STATUS_OK;
                if (lastChunk) {
                    MCLoggerFactory.getLogger(RandomBinaryTransmitter.class).debug("Last r.chunk delivered, removing transmission: " + uri);
                    if (task.isDeleteBlob()) {
                        BinaryStorable binary = Setup.get().getStorage().getBinary(task.getLocalUri());
                        Setup.get().getStorage().delete(binary);
                    }
                    storage.delete(RandomBinTransTask.RESENDABLE_METADATA, RandomBinTransTask.escapeUri(uri));
                    status = RandomTransmitterListener.RandomTransmitEvent.STATUS_FINISHED;
                } else {
                    // check if this is the chunk which we expect (otherwise it may be previous)
                    if (task.getCurrentChunkId() != null && chunkId.equals(task.getCurrentChunkId())) {
                        task.setPosition(task.getPosition() + task.getReadWhileLastProcess());
                        task.setReadWhileLastProcess(0);
                        task.setCurrentChunk(task.getCurrentChunk() + 1);
                        task.setCurrentChunkId(null);
                        storage.save(task);
                    }
                }
                fireListeners(new RandomTransmitterListener.RandomTransmitEvent(task.getLocalUri(),
                        task.getCount(), task.getCurrentChunk(), status));
            } finally {
                releaseTask(uri);
            }
            Resender.getInstance().resetRetransmissionTimeout(RandomBinTransTask.RESENDABLE_METADATA);
        } else {
            MCLoggerFactory.getLogger(RandomBinaryTransmitter.class).trace("Failed to acquire task permission upon chunk delivery: " + uri);
        }
    }

    private void initThreadPool() {
        if (!initialized) {
            try {
                initLock.acquire();
                if (!initialized) {
                    initialized = true;
                    int threadCount = Setup.get().getSettings().getIntProperty("bin.scanning.threads", "1");
                    threadPool = new PooledExecutor(new LinkedQueue(), threadCount);
                    threadPool.setMinimumPoolSize(threadCount);
                    threadPool.waitWhenBlocked();
                    threadPool.setThreadFactory(new ThreadFactory() {
                        private int threadNum = 1;

                        public Thread newThread(Runnable command) {
                            return new Thread(command, "BinaryTransmitter-" + threadNum++);
                        }
                    });
                    threadPool.setKeepAliveTime(-1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                initLock.release();
            }
        }
    }

    private void process(final RandomBinTransTask task) {
        try {
            threadPool.execute(new Runnable() {
                public void run() {
                    try {
                        processTask(task);
                    } catch (Exception e) {
                        MCLoggerFactory.getLogger(getClass()).error(e);
                        handleScanningError(e, task);
                    }
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleScanningError(Exception e, RandomBinTransTask task) {

        BinaryChunkResendable chunk = new BinaryChunkResendable();
        chunk.setOrderNumber(task.getCurrentChunk());
        chunk.setData((e.getMessage() + "\n" + e.getStackTrace()).getBytes());
        chunk.setUri(task.getUri());
        chunk.setErrorOccured(true);

        Resender.getInstance().send(chunk);

        finalizeTask(task);
    }

    private void signalTaskDetails(RandomBinTransTask task) {
        DefaultRpcResponseHandler.binTransDetails(task);
    }

    private void finalizeTask(RandomBinTransTask task) {
        task.setState(BinaryTransmissionTask.STATE_DONE);
        Setup.get().getStorage().save(task);
    }
}
