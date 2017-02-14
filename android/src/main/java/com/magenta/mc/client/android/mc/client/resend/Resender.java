package com.magenta.mc.client.android.mc.client.resend;

import com.magenta.mc.client.android.MobileApp;
import com.magenta.mc.client.android.mc.components.MCTimerTask;
import com.magenta.mc.client.android.mc.components.McTimeoutTask;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.setup.Setup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import EDU.oswego.cs.dl.util.concurrent.Mutex;
import EDU.oswego.cs.dl.util.concurrent.ReentrantWriterPreferenceReadWriteLock;

public class Resender {

    private static Resender instance;
    private int INTERVAL = 30000; // 30 seconds
    private MCTimerTask resendResendables;
    private Map resendableMap;
    private ReentrantWriterPreferenceReadWriteLock cacheLock = new ReentrantWriterPreferenceReadWriteLock();
    private ResendableMetadata[] resendablesMetadata;
    private Map retransmissionTime = new HashMap(); // millisecond (java.lang.Long) of last retransmission mapped by StorableMetadata
    private Mutex retransmissionLock = new Mutex();
    /**
     * Load cache from storage
     */
    private boolean cacheLoaded;

    private Resender() {
    }

    public static Resender getInstance() {
        if (instance == null) {
            instance = new Resender();
        }
        return instance;
    }

    public void registerResendables(ResendableMetadata[] resendablesMetadata) {
        this.resendablesMetadata = resendablesMetadata;
        try {
            cacheLock.writeLock().acquire();

            resendableMap = new HashMap();
            for (int i = 0; i < resendablesMetadata.length; i++) {
                resendableMap.put(resendablesMetadata[i], new ArrayList());
            }
        } catch (InterruptedException e) {
            // ok
        } finally {
            cacheLock.writeLock().release();
        }
    }

    private List getResendables(ResendableMetadata meta) {
        try {
            cacheLock.readLock().acquire();
            return (List) resendableMap.get(meta);
        } catch (InterruptedException e) {
            // ok
        } finally {
            cacheLock.readLock().release();
        }
        return new ArrayList();
    }

    private void send(boolean all) {
        for (int i = 0; i < resendablesMetadata.length; i++) {
            ResendableMetadata meta = resendablesMetadata[i];
            if (meta.consecutive) {
                if (all) {
                    retransmitConsecutive(meta);
                }
            } else {
                retransmit(meta);
            }
        }
    }

    public void start() {

        stop();

        resendResendables = new MCTimerTask() {

            public void runTask() {
                send(false);
            }

        };

        loadCacheIfNecessary();

        send(true);

        // seems like retransmission interval is not needed
        INTERVAL = Setup.get().getSettings().getIntProperty("resender.interval", "30000");
        MCLoggerFactory.getLogger(getClass()).debug("Resender started with interval: " + INTERVAL);
    }

    public void retransmit(ResendableMetadata meta) {
        if (meta.consecutive) {
            retransmitConsecutive(meta);
        } else {
            List resendables = getResendables(meta);

            if (resendables.size() > 0) {
                try {
                    meta.lock.readLock().acquire();

                    for (int k = 0; k < resendables.size(); k++) {
                        Resendable resendable = (Resendable) resendables.get(k);
                        MCLoggerFactory.getLogger(Resender.class).trace("sending " + k + " " + resendable);
                        resendable.send();
                    }
                } catch (InterruptedException e) {
                    // ok
                } finally {
                    meta.lock.readLock().release();
                }
            }
        }
    }

    private void retransmitConsecutive(ResendableMetadata meta) {
        MCLoggerFactory.getLogger(getClass()).trace("Consecutive retransmission requested " + meta.name);
        if (retransmissionTimeoutExpired(meta)) {
            boolean sent = false;
            Resendable resendable;
            if (meta.reverse) {
                resendable = (Resendable) Setup.get().getStorage().loadLast(meta);
            } else {
                resendable = (Resendable) Setup.get().getStorage().loadFirst(meta);
            }
            if (resendable != null) {
                if (meta.managed) {
                    sent = meta.send(resendable);
                } else {
                    sent = resendable.send();
                }
            } else
                MCLoggerFactory.getLogger(getClass()).trace("Consecutive retransmission forbidden " + meta.name);
            if (!sent) {
                resetRetransmissionTimeout(meta);
            }
        } else {
            MCLoggerFactory.getLogger(getClass()).debug("Consecutive retransmission skipped " + meta.name);
        }
    }

    private boolean retransmissionTimeoutExpired(ResendableMetadata meta) {
        boolean retransmit = false;
        try {
            retransmissionLock.acquire();
            Object timeObj = retransmissionTime.get(meta);
            if (timeObj != null && (System.currentTimeMillis() - ((Long) timeObj).longValue()) < INTERVAL) {
                // resendable was sent recently, keep waiting for response
            } else {
                retransmit = true;
                retransmissionTime.put(meta, new Long(System.currentTimeMillis()));
            }
        } catch (InterruptedException e) {
            // thread has been interrupted - seem to be closing application or session
        } finally {
            retransmissionLock.release();
        }
        return retransmit;
    }

    public boolean resetRetransmissionTimeout(ResendableMetadata meta) {
        boolean reset = false;
        try {
            retransmissionLock.acquire();
            reset = retransmissionTime.remove(meta) != null;
        } catch (InterruptedException e) {
            // thread has been interrupted - seem to be closing application or session
        } finally {
            retransmissionLock.release();
        }
        return reset;
    }

    private void resetRetransmissionTimeouts() {
        try {
            retransmissionLock.acquire();
            retransmissionTime.clear();
        } catch (InterruptedException e) {
            // thread has been interrupted - seem to be closing application or session
        } finally {
            retransmissionLock.release();
        }
    }

    public void stop() {
        if (resendResendables != null) {
            resendResendables.cancel();
            resendResendables = null;
        }
        cancelErrorTimeouts();
        resetRetransmissionTimeouts();
    }

    public void clearCache(ResendableMetadata metadata) {
        List resendableList = getResendables(metadata);

        if (resendableList != null) {
            try {
                metadata.lock.writeLock().acquire();

                for (int i = 0; i < resendableList.size(); i++) {
                    Setup.get().getStorage().delete((Resendable) resendableList.get(i));
                }
                resendableList.clear();
            } catch (InterruptedException e) {
                // ok
            } finally {
                metadata.lock.writeLock().release();
            }
        }
    }

    public void loadCacheIfNecessary() {
        if (!cacheLoaded) {
            try {
                cacheLock.writeLock().acquire();
                if (!cacheLoaded) {
                    for (int i = 0; i < resendablesMetadata.length; i++) {
                        List resendables = Setup.get().getStorage().load(resendablesMetadata[i]);
                        if (resendables.size() > 0) {
                            resendableMap.put(((Resendable) resendables.get(0)).getMetadata(), resendables);
                        }
                    }
                    cacheLoaded = true;
                }
            } catch (InterruptedException e) {
                // ok
            } finally {
                cacheLock.writeLock().release();
            }
        }
    }

    /**
     * Send resendable to server and save to storage. Resendable will resend every 30 sec until send method invokation.
     *
     * @param resendable
     */
    public void send(Resendable resendable) {
        MCLoggerFactory.getLogger(getClass()).trace("send " + resendable);
        saveResendable(resendable);
        sendSavedResendable(resendable);
    }


    /**
     * Enable resending and try to send resendable
     *
     * @param resendable
     */
    public void sendSavedResendable(Resendable resendable) {
        MCLoggerFactory.getLogger(getClass()).trace("sendSaved " + resendable);
        if (((ResendableMetadata) resendable.getMetadata()).consecutive) {
            retransmitConsecutive(((ResendableMetadata) resendable.getMetadata()));
        } else {
            resendable.send();
        }
    }

    /**
     * Save resendable into storage
     *
     * @param resendable
     */
    public void saveResendable(Resendable resendable) {
        if (resendable.getId() == null) {
            resendable.setId("" + ResendableIdGenerator.generateId());
        }
        if (!((ResendableMetadata) resendable.getMetadata()).consecutive) {
            try {
                cacheLock.writeLock().acquire();

                List resendableList = (List) resendableMap.get(resendable.getMetadata());
                if (resendableList == null) {
                    resendableList = new ArrayList();
                    resendableMap.put(resendable.getMetadata(), resendableList);
                }
                resendableList.add(resendable);
            } catch (InterruptedException e) {
                // ok
            } finally {
                cacheLock.writeLock().release();
            }
        }
        MCLoggerFactory.getLogger(getClass()).trace("save " + resendable);
        Setup.get().getStorage().save(resendable);
    }

    /**
     * Invoked from RPC handler to remove resendable from cache and storage.
     *
     * @param metadata
     * @param id
     */
    public void sent(final ResendableMetadata metadata, Long id) {
        sent(metadata, id, new Object[]{});
    }

    /**
     * Invoked from RPC handler to remove resendable from cache and storage.
     *
     * @param metadata
     * @param id
     */
    public void sent(final ResendableMetadata metadata, Long id, Object[] params) {
        if (metadata.consecutive) {
            if (metadata.managed) {
                metadata.sent(metadata, id.toString(), params);
            } else {
                Setup.get().getStorage().delete(metadata, id.toString());
            }
            resetRetransmissionTimeout(metadata);
            MobileApp.runTask(new Runnable() {
                public void run() {
                    retransmitConsecutive(metadata);
                }
            });
        } else {
            List resendableList = getResendables(metadata);
            if (resendableList != null) {
                try {
                    metadata.lock.writeLock().acquire();

                    ListIterator iterator = resendableList.listIterator();
                    while (iterator.hasNext()) {
                        Resendable next = (Resendable) iterator.next();
                        if (Long.parseLong(next.getId()) == id.longValue()) {
                            iterator.remove();
                            Setup.get().getStorage().delete(next);
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    // ok
                } finally {
                    metadata.lock.writeLock().release();
                }
            }
        }
    }

    private void cancelErrorTimeouts() {
        for (int i = 0; i < resendablesMetadata.length; i++) {
            ResendableMetadata metadata = resendablesMetadata[i];
            try {
                metadata.lock.readLock().acquire();

                if (metadata.errorTimeout != null) {
                    metadata.errorTimeout.cancel();
                    metadata.errorTimeout = null;
                }
            } catch (InterruptedException e) {
                // ok
            } finally {
                metadata.lock.readLock().release();
            }
        }
    }

    private void scheduleNextErrorTimeout(final ResendableMetadata metadata, final Runnable task) {
        try {
            metadata.lock.readLock().acquire();

            if (metadata.errorTimeout != null) {
                metadata.errorTimeout.cancel();
            }
            metadata.errorTimeout = new McTimeoutTask(new Runnable() {
                public void run() {
                    metadata.errorTimeout = null;
                    task.run();
                }
            }, INTERVAL);
        } catch (InterruptedException e) {
            // ok
        } finally {
            metadata.lock.readLock().release();
        }
    }

    /**
     * Invoked from RPC handler to remove resendable from cache and storage.
     *
     * @param metadata
     * @param id
     */
    public void error(final ResendableMetadata metadata, Long id) {
        if (metadata.consecutive) {
            scheduleNextErrorTimeout(metadata, new Runnable() {
                public void run() {
                    resetRetransmissionTimeout(metadata);
                    retransmitConsecutive(metadata);
                }
            });
        } else {
            List resendableList = getResendables(metadata);
            if (resendableList != null) {
                try {
                    metadata.lock.readLock().acquire();

                    ListIterator iterator = resendableList.listIterator();
                    while (iterator.hasNext()) {
                        final Resendable next = (Resendable) iterator.next();
                        if (Long.parseLong(next.getId()) == id.longValue()) {
                            scheduleNextErrorTimeout(metadata, new Runnable() {
                                public void run() {
                                    resetRetransmissionTimeout(metadata);
                                    retransmitConsecutive(metadata);
                                }
                            });
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    // ok
                } finally {
                    metadata.lock.readLock().release();
                }
            }
        }
    }

}
