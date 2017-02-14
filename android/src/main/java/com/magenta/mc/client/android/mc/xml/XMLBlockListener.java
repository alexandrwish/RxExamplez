package com.magenta.mc.client.android.mc.xml;

/**
 * Created 30.07.2010
 *
 * @author Konstantin Pestrikov
 */
public interface XMLBlockListener {
    int ERROR = -1;
    int BLOCK_REJECTED = 0;
    int BLOCK_PROCESSED = 1;
    int NO_MORE_BLOCKS = 2;

    /**
     * Method to handle an incomming block.
     *
     * @parameter data The incomming block
     */

    int blockArrived(XMLDataBlock data);
}
