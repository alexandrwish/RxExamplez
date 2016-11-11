/*
 * Utf8IOStream.java
 *
 * Created on 18.12.2005, 0:52
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.magenta.mc.client.io;

//#if ZLIB

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZInputStream;
import com.jcraft.jzlib.ZOutputStream;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.util.Strconv;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

//import javax.microedition.io.*;

/**
 * @author EvgS
 *         <p>
 *         this stream receives UTF-8-encoded bytes,
 *         so they need to be decoded afterwards
 */
public class Utf8IOStream {

    //private StreamConnection connection;
    private Socket socket;
    private InputStream inpStream;
    private OutputStream outStream;

    private boolean iStreamWaiting;

    private int bytesRecv;

    private int bytesSent;


    //#if (ZLIB)

    /**
     * Creates a new instance of Utf8IOStream
     */
    public Utf8IOStream(Socket socket) throws IOException {
        this.socket = socket;
        try {
            socket.setKeepAlive(Setup.get().getSettings().getBooleanProperty("socket.keepalive", "true"));
            socket.setSoLinger(true, Setup.get().getSettings().getIntProperty("socket.linger", "60"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        inpStream = socket.getInputStream();
        outStream = socket.getOutputStream();

    }
//#endif

    /**
     * Creates a new instance of Utf8IOStream
     */
    /*public Utf8IOStream(StreamConnection connection) throws IOException {
        this.connection = connection;
        try {
            SocketConnection sc = (SocketConnection) connection;
            sc.setSocketOption(SocketConnection.KEEPALIVE, Setup.get().getSettings().getBooleanProperty("socket.keepalive", "true") ? 1 : 0);
            sc.setSocketOption(SocketConnection.LINGER, Setup.get().getSettings().getIntProperty("socket.linger", "60"));
            *//*if (cf.socketLINGER>=0) sc.setSocketOption(SocketConnection.LINGER, cf.socketLINGER);
            if (cf.socketRCVBUF>=0) sc.setSocketOption(SocketConnection.RCVBUF, cf.socketRCVBUF);
            if (cf.socketSNDBUF>=0) sc.setSocketOption(SocketConnection.SNDBUF, cf.socketSNDBUF);*//*
        } catch (Exception e) {
        }

        inpStream = connection.openInputStream();
        outStream = connection.openOutputStream();

    }*/
    public void setStreamCompression() {
        inpStream = new ZInputStream(inpStream);
        outStream = new ZOutputStream(outStream, JZlib.Z_DEFAULT_COMPRESSION);
        ((ZOutputStream) outStream).setFlushMode(JZlib.Z_SYNC_FLUSH);
    }

    public void send(StringBuffer data) throws IOException {
        String logStr = data.toString();
        if (logStr.length() > 10000) {
            logStr = "<long packet(" + logStr.length() + " symbols), truncated in log to 100 symbols>" + logStr.substring(0, 100);
        } else if (logStr.indexOf("type=\"set\" id=\"binChunk:") > 0) {
            logStr = "<long packet(" + logStr.length() + " symbols), truncated in log to 100 symbols>" + logStr.substring(0, 100);
        }
        MCLoggerFactory.getLogger(getClass()).debug("sending data: " + logStr);

        synchronized (outStream) {
            MCLoggerFactory.getLogger(getClass()).trace("got outStream, writing");

            StringBuffer outbuf = Strconv.toUTFSb(data);
            int outLen = outbuf.length();
            byte bytes[] = new byte[outLen];
            for (int i = 0; i < outLen; i++) {
                bytes[i] = (byte) outbuf.charAt(i);
            }

            try {
                outStream.write(bytes);
            } catch (Exception e) {
                if (e instanceof IOException) {
                    throw (IOException) e;
                } else {
                    MCLoggerFactory.getLogger(getClass()).debug("Exception while writing to stream: ", e);
                    throw new IOException(e.getMessage());
                }
            }
            bytesSent += outLen;

            outStream.flush();
        }
//#if (XML_STREAM_DEBUG)        
//#         MCLoggerFactory.getLogger(getClass()).debug(">> "+data);
//#endif
    }


    public int read(byte buf[]) throws IOException {
        int avail = inpStream.available();

        if (avail == 0)
//#if !ZLIB
//#             //trying to fix phillips 9@9
//#             if (!Config.getInstance().istreamWaiting) avail=1;
//#             else
//#endif            
        {
            return 0;
        }

        if (avail > buf.length) {
            avail = buf.length;
        }

        avail = inpStream.read(buf, 0, avail);

        bytesRecv += avail;
        return avail;
    }


    public void close(boolean asynchronousClose) {

        Runnable closeStreamsRunnable = new Runnable() {
            public void run() {
                try {
                    inpStream.close();
                } catch (Exception e) {
                }
                try {
                    outStream.close();
                } catch (Exception e) {
                }
            }
        };


        if (asynchronousClose) {
            new Thread(closeStreamsRunnable).start();
            inpStream = null;
            outStream = null;
        } else {
            closeStreamsRunnable.run();
        }
        /*if (connection != null) {
            try {
                connection.close();
            }  catch (Exception e) {}
        }*/
    }

    public void closeSocket() {
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (Exception e) {
            //
        }
    }

    //#if ZLIB

    private void appendZlibStats(StringBuffer s, long packed, long unpacked, boolean read) {
        s.append(packed).append(read ? ">>>" : "<<<").append(unpacked);
        String ratio = Long.toString((10 * unpacked) / packed);
        int dotpos = ratio.length() - 1;

    }

    public String getStreamStats() {
        StringBuffer stats = new StringBuffer();
        int sent = this.bytesSent;
        int recv = this.bytesRecv;
        if (inpStream instanceof ZInputStream) {
            ZInputStream z = (ZInputStream) inpStream;
            recv += z.getTotalIn() - z.getTotalOut();
            ZOutputStream zo = (ZOutputStream) outStream;
            sent += zo.getTotalOut() - zo.getTotalIn();
            stats.append("ZLib:\nin=");
            appendZlibStats(stats, z.getTotalIn(), z.getTotalOut(), true);
            stats.append("\nout=");
            appendZlibStats(stats, zo.getTotalOut(), zo.getTotalIn(), false);
        }
        stats.append("\nStream:\nin=").append(recv)
                .append("\nout=").append(sent)
                .append("\n\n");
        try {
            stats.append(socket.getLocalAddress())
                    .append(":")
                    .append(socket.getLocalPort())
                    .append("->")
                    .append(socket.getInetAddress())
                    .append(":")
                    .append(socket.getPort());
            /*stats.append(((SocketConnection) connection).getLocalAddress())
                    .append(":")
                    .append(((SocketConnection) connection).getLocalPort())
                    .append("->")
                    .append(((SocketConnection) connection).getAddress())
                    .append(":")
                    .append(((SocketConnection) connection).getPort());*/
        } catch (Exception ex) {
            stats.append("unknown");
        }

        return stats.toString();
    }
//#endif
}
