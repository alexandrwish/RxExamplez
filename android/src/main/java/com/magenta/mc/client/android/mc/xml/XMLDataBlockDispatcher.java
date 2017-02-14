package com.magenta.mc.client.android.mc.xml;

import java.util.Vector;

/**
 * Created 30.07.2010
 *
 * @author Konstantin Pestrikov
 */
public class XMLDataBlockDispatcher extends Thread {

    private static int threadNum = 1;
    /**
     * The list of messages waiting to be dispatched
     */

    protected Vector waitingQueue = new Vector();
    private Vector blockListeners = new Vector();
    /**
     * Flag to watch the dispatching loop
     */

    private boolean dispatcherActive;

    /**
     * Constructor to start the dispatcher in a thread.
     */
    public XMLDataBlockDispatcher() {
        super("XMLDataBlockDispatcherThread-" + threadNum++);
        start();
    }

    public boolean isActive() {
        return dispatcherActive;
    }

    public void addBlockListener(XMLBlockListener listener) {
        synchronized (blockListeners) {
            if (blockListeners.indexOf(listener) > 0) {
                return;
            }
            blockListeners.addElement(listener);
        }
    }

    public void cancelBlockListener(XMLBlockListener listener) {
        synchronized (blockListeners) {
            try {
                blockListeners.removeElement(listener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void cancelBlockListenerByClass(Class removeClass) {
        synchronized (blockListeners) {
            int index = 0;
            while (index < blockListeners.size()) {
                Object listener = blockListeners.elementAt(index);
                if (listener.getClass().equals(removeClass)) {
                    blockListeners.removeElementAt(index);
                } else {
                    index++;
                }
            }
        }
    }


    /**
     * Method to add a datablock to the dispatch queue
     *
     * @param datablock The block to add
     */

    public void broadcastXmlDataBlock(XMLDataBlock dataBlock) {
        waitingQueue.addElement(dataBlock);
    }

    /**
     * The thread loop that handles dispatching any waiting datablocks
     */

    public void run() {
        dispatcherActive = true;
        while (dispatcherActive) {
            while (waitingQueue.size() == 0) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                }
            }

            XMLDataBlock dataBlock = (XMLDataBlock) waitingQueue.elementAt(0);
            waitingQueue.removeElementAt(0);
            notifyListeners(dataBlock);
        }
    }

    protected int notifyListeners(XMLDataBlock dataBlock) {
        int i = 0;
        try {
            int processResult = XMLBlockListener.BLOCK_REJECTED;
            synchronized (blockListeners) {
                while (i < blockListeners.size()) {
                    processResult = ((XMLBlockListener) blockListeners.elementAt(i)).blockArrived(dataBlock);
                    if (processResult == XMLBlockListener.BLOCK_PROCESSED) {
                        break;
                    }
                    if (processResult == XMLBlockListener.NO_MORE_BLOCKS) {
                        blockListeners.removeElementAt(i);
                        break;
                    }
                    i++;
                }
            }
            return processResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return XMLBlockListener.ERROR;
    }

    /**
     * Method to stop the dispatcher
     */

    public void halt() {
        dispatcherActive = false;
    }
}
