package com.magenta.mc.client.android.rpc.bin_chunks;

import com.magenta.mc.client.android.mc.setup.Setup;

import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;
import EDU.oswego.cs.dl.util.concurrent.Mutex;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import EDU.oswego.cs.dl.util.concurrent.ThreadFactory;

public class BinaryTransmitter {

    private final static Mutex initLock = new Mutex();
    private static boolean initialized;

    private BinaryTransmitter() {
        initThreadPool();
    }

    private void initThreadPool() {
        if (!initialized) {
            try {
                initLock.acquire();
                if (!initialized) {
                    initialized = true;
                    int threadCount = Setup.get().getSettings().getIntProperty("bin.scanning.threads", "1");
                    PooledExecutor threadPool = new PooledExecutor(new LinkedQueue(), threadCount);
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
}