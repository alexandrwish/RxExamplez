/*
  Copyright (c) 2000,2001 Al Sutton (al@alsutton.com)
  All rights reserved.
  Redistribution and use in source and binary forms, with or without modification, are permitted
  provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice, this list of conditions
  and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright notice, this list of
  conditions and the following disclaimer in the documentation and/or other materials provided with
  the distribution.

  Neither the name of Al Sutton nor the names of its contributors may be used to endorse or promote
  products derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS ``AS IS'' AND ANY EXPRESS OR
  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
  THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.magenta.mc.client.android.rpc.xmpp;

import com.magenta.mc.client.android.mc.xml.XMLBlockListener;
import com.magenta.mc.client.android.mc.xml.XMLDataBlock;
import com.magenta.mc.client.android.mc.xml.XMLDataBlockDispatcher;
import com.magenta.mc.client.android.rpc.xmpp.datablocks.Iq;

/**
 * The dispatcher for blocks that have arrived. Adds new blocks to the
 * dispatch queue, and then dispatches waiting blocks in their own thread to
 * avoid holding up the stream reader.
 */

public class XMPPDataBlockDispatcher extends XMLDataBlockDispatcher {
    /**
     * The recipient waiting on this stream
     */

    private XMPPListener listener = null;

    private XMPPStream stream;

    /**
     * Constructor to start the dispatcher in a thread.
     */

    public XMPPDataBlockDispatcher(XMPPStream stream) {
        super();
        this.stream = stream;
    }

    protected int notifyListeners(XMLDataBlock dataBlock) {
        int processResult = super.notifyListeners(dataBlock);

        if (processResult == XMLBlockListener.BLOCK_REJECTED) {
            if (listener != null) {
                processResult = listener.blockArrived(dataBlock);
            }
        }

        if (processResult == XMLBlockListener.BLOCK_REJECTED) {
            if (!(dataBlock instanceof Iq)) {
                return processResult;
            }
            String type = dataBlock.getTypeAttribute();
            if (type.equals("get") || type.equals("set")) {
                dataBlock.setAttribute("to", dataBlock.getAttribute("from"));
                dataBlock.setAttribute("from", null);
                dataBlock.setTypeAttribute("error");
                dataBlock.addChild(new XmppError(XmppError.FEATURE_NOT_IMPLEMENTED, null).construct());
                stream.send(dataBlock);
            }
            //TODO: reject iq stansas where type =="get" | "set"
        }
        return processResult;
    }

    /**
     * Set the listener that we are dispatching to. Allows for switching
     * of clients in mid stream.
     *
     * @param _listener The listener to dispatch to.
     */

    public void setJabberListener(XMPPListener _listener) {
        listener = _listener;
    }


    /**
     * Method to add a datablock to the dispatch queue
     *
     * @param datablock The block to add
     */

    public void broadcastJabberDataBlock(XMLDataBlock dataBlock) {
        waitingQueue.addElement(dataBlock);
    }


    public void rosterNotify() {
        listener.rosterItemNotify();
    }

    /**
     * Method to tell the listener the connection has been terminated
     *
     * @param exception The exception that caused the termination. This may be
     *                  null for the situtations where the connection has terminated without an
     *                  exception.
     */

    public void broadcastTerminatedConnection(Exception exception, long connectionId) {
        halt();
        if (listener != null) {
            listener.connectionTerminated(exception, connectionId);
        }
    }

    /**
     * Method to tell the listener the stream is ready for talking to.
     */

    public void broadcastBeginConversation() {
        if (listener != null) {
            listener.beginConversation();
        }
    }

}
