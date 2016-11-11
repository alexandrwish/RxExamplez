package com.magenta.mc.client.bin_chunks;

import com.magenta.mc.client.bin_chunks.progress.BinaryTransmissionProgress;
import com.magenta.mc.client.client.resend.ResendableIdGenerator;
import com.magenta.mc.client.client.resend.Resender;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.storage.BinaryStorable;
import com.magenta.mc.client.storage.Storable;
import com.magenta.mc.client.storage.Storage;
import com.magenta.mc.client.xmpp.extensions.rpc.DefaultRpcResponseHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;
import EDU.oswego.cs.dl.util.concurrent.Mutex;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import EDU.oswego.cs.dl.util.concurrent.ThreadFactory;

/**
 * Created by IntelliJ IDEA.
 * User: const
 * Date: 08.06.12
 * Time: 18:46
 * To change this template use File | Settings | File Templates.
 */
public class BinaryTransmitter {
    private static final long BINARY_TRANSMISSION_EXPIRATION_7_DAYS = 7 * 24 * 60 * 60 * 1000;
    private static Mutex initLock = new Mutex();
    private static boolean initialized;

    // a thread pool with unbounded queue and limited number of threads
    // is going be used for scanning of binaries
    private static PooledExecutor threadPool;
    private static Mutex taskProcessMutex = new Mutex();
    private static Map runningTasks = new HashMap();
    private final int chunkMaxLength;

    public BinaryTransmitter() {
        chunkMaxLength = Setup.get().getSettings().getIntProperty("bin.chunk.length", "50000");
        initThreadPool();
    }

    public static void shutdown() {
        if (initialized && threadPool != null) {
            threadPool.shutdownNow();
            threadPool = null;
            initialized = false;
        }
    }

    public static boolean transmit(String uri, String localUri, boolean deleteBlobUponTransmission) {
        BinaryTransmissionTask transmissionTask = new BinaryTransmissionTask(uri, localUri, deleteBlobUponTransmission);
        Storage storage = Setup.get().getStorage();
        Storable prevTask = storage.load(BinaryTransmissionTask.STORABLE_METADATA, transmissionTask.getId());
        if (prevTask != null) {
            return false;
        } else {
            storage.save(transmissionTask);
            new BinaryTransmitter().process(transmissionTask);
        }
        return true;
    }

    private static boolean acquireTaskRun(String url) {
        try {
            taskProcessMutex.acquire();
            if (runningTasks.containsKey(url)) {
                MCLoggerFactory.getLogger(BinaryTransmitter.class).debug("binary scanning in progress, permission denied: " + url);
                return false;
            } else {
                runningTasks.put(url, Boolean.TRUE);
                MCLoggerFactory.getLogger(BinaryTransmitter.class).debug("binary scanning permission granted: " + url);
                return true;
            }
        } catch (InterruptedException e) {
            MCLoggerFactory.getLogger(BinaryTransmitter.class).debug("acquireTaskRun interrupted, skipping");
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
            MCLoggerFactory.getLogger(BinaryTransmitter.class).debug("binary scanning permission released: " + url);
            taskProcessMutex.release();
        }
    }

    public static void transDetailsReceived(String url) {
        Storage storage = Setup.get().getStorage();
        BinaryTransmissionTask task = (BinaryTransmissionTask) storage.load(BinaryTransmissionTask.STORABLE_METADATA, BinaryTransmissionTask.escapeUri(url));
        if (task == null) {
            MCLoggerFactory.getLogger(BinaryTransmitter.class).warn("Signalled binary task not found upon confirmation: " + url);
            return;
        }
        task.setState(BinaryTransmissionTask.STATE_SIGNALLED);

        //task.setDateChanged(Setup.get().getSettings().getCurrentDate());

        if (task.isDeleteBlob()) {
            BinaryStorable binary = Setup.get().getStorage().getBinary(task.getLocalUri());
            Setup.get().getStorage().delete(binary);
        }
        storage.save(task);
        MCLoggerFactory.getLogger(BinaryTransmitter.class).debug("Binary task signalled: " + url);
        //Resender.getInstance().resetRetransmissionTimeout(BinaryChunkResendable.METADATA);
        Resender.getInstance().retransmit(BinaryChunkResendable.METADATA);
    }

    public static void chunkDelivered(Long chunkId, String uri, boolean lastChunk) {
        if (lastChunk) {
            MCLoggerFactory.getLogger(BinaryTransmitter.class).debug("Last chunk delivered, removing transmission: " + uri);
            Setup.get().getStorage().delete(BinaryTransmissionTask.STORABLE_METADATA, BinaryTransmissionTask.escapeUri(uri));
        }
        BinaryTransmissionProgress.getInstance().chunkSent(uri);
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

    private void process(final BinaryTransmissionTask task) {
        try {
            threadPool.execute(new Runnable() {
                public void run() {
                    try {
                        scan(task);
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

    private void handleScanningError(Exception e, BinaryTransmissionTask task) {
        int index = task.getCount();
        if (index == -1) { //if this is first chunk for task
            index = 0;
        }
        task.setCount(index + 1);

        BinaryChunkResendable chunk = new BinaryChunkResendable();
        chunk.setOrderNumber(index);
        chunk.setData((e.getMessage() + "\n" + e.getStackTrace()).getBytes());
        chunk.setUri(task.getUri());
        chunk.setErrorOccured(true);

        Resender.getInstance().send(chunk);

        finalizeTask(task);
    }

    public void checkTasks() {
        try {
            Storage storage = Setup.get().getStorage();
            List storedTasks = storage.load(BinaryTransmissionTask.STORABLE_METADATA);
            for (int i = 0; i < storedTasks.size(); i++) {
                BinaryTransmissionTask task = (BinaryTransmissionTask) storedTasks.get(i);
                switch (task.getState()) {
                    case BinaryTransmissionTask.STATE_RECEIVED:
                        String url = task.getUri();
                        try {
                            if (acquireTaskRun(url)) {
                                try {
                                    if (BinaryTransmissionTask.STATE_RECEIVED == task.getState()) { // recheck within critical section
                                        // task has not been successfully processed - probably due to application/system crash

                                        // delete existing chunks (though some of them may have been already sent)
                                        int count = task.getCount();
                                        if (count > 0) {
                                            for (int p = 0; p < count; p++) {
                                                try {
                                                    String pieceId = (String) task.getChunks().get(p);
                                                    storage.delete(BinaryChunkResendable.METADATA, pieceId);
                                                } catch (IndexOutOfBoundsException e) {
                                                    break;
                                                }
                                            }
                                        }
                                        task.setCount(-1);
                                        task.getChunks().clear();
                                        storage.save(task);
                                        // now re-run log parsing into pieces
                                        process(task);
                                    }
                                } finally {
                                    releaseTask(url);
                                }
                            }
                        } catch (Exception e) {
                            MCLoggerFactory.getLogger(getClass()).error(e);
                            handleScanningError(e, task);
                        }
                        break;
                    case BinaryTransmissionTask.STATE_DONE:
                        // task is processed, but count signal was not confirmed - signal piece count
                        signalTaskDetails(task);
                        break;
                    case BinaryTransmissionTask.STATE_SIGNALLED:
                        // this is an old accomplished task, just check and
                        // delete requests older than 2 days
                        long elapsedMillis = Setup.get().getSettings().getCurrentDate().getTime() - task.getCreationDate().getTime();
                        if (elapsedMillis > BINARY_TRANSMISSION_EXPIRATION_7_DAYS) {
                            storage.delete(task);
                        }
                        break;
                }
            }
        } catch (Exception e) {
            MCLoggerFactory.getLogger(getClass()).error("Unexpected error while checking log requests", e);
        }

    }

    private void scan(BinaryTransmissionTask task) throws IOException {
        String taskUrl = task.getUri();
        if (acquireTaskRun(taskUrl)) {
            InputStream inputStream = null;
            try {

                byte[] buffer = new byte[chunkMaxLength];
                int read;
                int tail = 0;
                BinaryStorable binary = Setup.get().getStorage().getBinary(task.getLocalUri());

                inputStream = binary.getInputStream();

                while ((read = inputStream.read(buffer, tail, chunkMaxLength - tail)) > 0) {
                    if (read + tail < chunkMaxLength) {
                        tail = read + tail;
                    } else {
                        nextChunk(task, buffer, chunkMaxLength);
                        tail = 0;
                    }
                }

                if (tail > 0) {
                    nextChunk(task, buffer, tail);
                }

                finalizeTask(task);
            } finally {
                releaseTask(taskUrl);
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception exc) {
                        // ok
                    }
                }
            }

            BinaryTransmissionProgress.getInstance().transmissionScanned(task.getUri(), task.getCount());

            signalTaskDetails(task);

            // time to cleanup
            System.gc();
        }
    }

    private void sleep() {
        // give a little breathe to the system and GC
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // fall through
        }
    }

    private void signalTaskDetails(BinaryTransmissionTask task) {
        DefaultRpcResponseHandler.binTransDetails(task);
    }

    private void finalizeTask(BinaryTransmissionTask task) {
        task.setState(BinaryTransmissionTask.STATE_DONE);
        //task.setDateChanged(Setup.get().getSettings().getCurrentDate());
        Setup.get().getStorage().save(task);
    }

    private void nextChunk(BinaryTransmissionTask task, byte[] data, int length) {
        int index = task.getCount();
        if (index == -1) { //if this is first chunk for request
            index = 0;
        }

        long chunkId = ResendableIdGenerator.generateId();

        task.setCount(index + 1);
        task.getChunks().add(new Long(chunkId));
        Setup.get().getStorage().save(task);

        BinaryChunkResendable chunk = new BinaryChunkResendable();
        chunk.setId(Long.toString(chunkId));
        chunk.setUri(task.getUri());
        chunk.setOrderNumber(index);
        byte[] resultData = new byte[length];
        System.arraycopy(data, 0, resultData, 0, length);
        chunk.setData(resultData);

        Resender.getInstance().saveResendable(chunk);

        sleep();
    }
}
