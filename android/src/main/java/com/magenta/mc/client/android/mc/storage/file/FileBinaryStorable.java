package com.magenta.mc.client.android.mc.storage.file;

import com.magenta.mc.client.android.mc.exception.StorageException;
import com.magenta.mc.client.android.mc.storage.BinaryStorable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * Created by IntelliJ IDEA.
 * User: const
 * Date: 09.06.12
 * Time: 20:45
 * To change this template use File | Settings | File Templates.
 */
public class FileBinaryStorable implements BinaryStorable {
    private final String storableName;
    private final String id;
    private final File file;
    private boolean loaded;
    private byte[] data = new byte[0];

    public FileBinaryStorable(String storableName, String id, File storageFolder) {
        this.storableName = storableName;
        this.id = id;
        File typeFolder = new File(storageFolder, storableName);
        if (!typeFolder.exists() && !typeFolder.mkdirs()) {
            throw new StorageException("Cannot create storage folder: " + typeFolder.getPath());
        }
        this.file = new File(typeFolder, id);
    }

    public String getStorableName() {
        return storableName;
    }

    public String getUri() {
        return "blob:" + storableName + "/" + id;
    }

    public String getId() {
        return id;
    }

    public long length() {
        return file.length();
    }

    private void load() {
        if (!loaded) {
            byte[] buff = new byte[8192];
            int length = 0;
            int read;
            try {
                while ((read = getInputStream().read(buff, length, buff.length - length)) > 0) {
                    if (read == (buff.length - length)) {
                        byte[] newBuff = new byte[buff.length * 2];
                        System.arraycopy(buff, 0, newBuff, 0, buff.length);
                        buff = newBuff;
                        length = buff.length;
                    } else {
                        length = length + read;
                    }
                }
                data = new byte[length];
                System.arraycopy(buff, 0, data, 0, length);
                loaded = true;
            } catch (IOException e) {
                throw new StorageException("Failed to load storage file: " + file.getPath(), e);
            }
        }
    }

    public void unload() {
        data = null;
        loaded = false;
    }

    public byte[] getData() {
        load();
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    private void checkFile() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new StorageException("Cannot create binary file: " + file.getPath(), e);
            }
        }
    }

    public InputStream getInputStream() {
        checkFile();
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new StorageException("Cannot open binary file: " + file.getPath(), e);
        }
    }

    public OutputStream getOutputStream() {
        checkFile();
        try {
            return new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new StorageException("Cannot open binary file: " + file.getPath(), e);
        }
    }

    public int read(int srcPos, byte[] dest, int destPos, int length) throws IOException {
        RandomAccessFile rFile = new RandomAccessFile(file, "r");
        rFile.seek(srcPos);
        int read = rFile.read(dest, destPos, length);
        rFile.close();
        return read;
    }

    public void save() {
        OutputStream outputStream = null;
        try {
            outputStream = getOutputStream();
            outputStream.write(data);
        } catch (IOException e) {
            throw new StorageException("Failed to write binary file: " + file.getPath(), e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    // oy
                }
            }
        }
    }
}
