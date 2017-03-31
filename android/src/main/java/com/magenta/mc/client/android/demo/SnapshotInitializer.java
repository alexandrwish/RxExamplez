package com.magenta.mc.client.android.demo;

import com.magenta.mc.client.android.log.MCLoggerFactory;
import com.magenta.mc.client.android.setup.Setup;
import com.magenta.mc.client.android.util.ResourceManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class SnapshotInitializer implements DemoStorageInitializer {

    private static final int BUFFER_SIZE = 2048;

    public boolean initStorage() {
        File storageDir = Setup.get().getStorage().getStorageDir();
        clearStorage(storageDir);
        try {
            InputStream demoStorageStream = ResourceManager.getInstance().getResourceAsStream("demo_storage.zip");
            if (demoStorageStream != null) {
                extractFolder(demoStorageStream, storageDir);
            } else {
                return false;
            }
        } catch (IOException e) {
            MCLoggerFactory.getLogger(getClass()).error("can't initialize demo storage", e);
            return false;
        }
        return true;
    }

    private void clearStorage(File storageDir) {
        if (storageDir.exists()) {
            delete(storageDir);
            storageDir.mkdir();
        }
    }

    protected void delete(File storageDir) {
        File[] files = storageDir.listFiles();
        for (File file : files) {
            if (file.isDirectory() && file.list().length > 0) {
                delete(file);
            }
            file.delete();
        }
        storageDir.delete();
    }

    private void extractFolder(InputStream inputStream, File destination) throws IOException {
        byte buffer[] = new byte[BUFFER_SIZE];
        ZipInputStream zipStream = new ZipInputStream(inputStream);
        if (!destination.exists()) {
            destination.mkdir();
        } else {
            File[] children = destination.listFiles();
            for (File aChildren : children) {
                aChildren.delete();
            }
        }
        ZipEntry entry;
        while ((entry = zipStream.getNextEntry()) != null) {
            String entryName = entry.getName();
            File destFile = new File(destination, entryName);
            destFile.getParentFile().mkdirs();
            if (!entry.isDirectory()) {
                FileOutputStream fos = new FileOutputStream(destFile);
                BufferedOutputStream destFileStream = new BufferedOutputStream(fos, BUFFER_SIZE);
                int read;
                while ((read = zipStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
                    destFileStream.write(buffer, 0, read);
                }
                destFileStream.flush();
                destFileStream.close();
            }
        }
        zipStream.close();
    }
}