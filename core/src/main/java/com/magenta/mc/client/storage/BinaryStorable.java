package com.magenta.mc.client.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: const
 * Date: 09.06.12
 * Time: 20:03
 * To change this template use File | Settings | File Templates.
 */
public interface BinaryStorable {
    String getId();

    byte[] getData();

    void setData(byte[] data);

    InputStream getInputStream();

    OutputStream getOutputStream();

    int read(int srcPos, byte[] dest, int destPos, int length) throws IOException;

    void save();

    long length();

    String getStorableName();

    String getUri();
}
