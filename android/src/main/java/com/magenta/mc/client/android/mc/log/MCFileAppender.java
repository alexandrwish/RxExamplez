package com.magenta.mc.client.android.mc.log;

import com.magenta.mc.client.android.mc.setup.Setup;

import net.sf.microlog.core.appender.AbstractFileAppender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MCFileAppender extends AbstractFileAppender {

    private static final String DIR_NAME_PROPERTY = "directory";
    private static final String[] MC_PROPERTY_NAMES = {DIR_NAME_PROPERTY};

    protected File file;

    public MCFileAppender() {
        super();
    }

    protected String createFileURI() {
        StringBuilder fileURIStringBuffer = new StringBuilder(DEFAULT_STRING_BUFFER_SIZE);
        boolean fileNameContainsPath = (fileName.indexOf('/') != -1) || (fileName.indexOf('\\') != -1);
        if (!fileNameContainsPath && directory == null) {
            setDirectoryAsFirstRoot();
        }
        if (directory != null) {
            fileURIStringBuffer.append(directory).append(File.separator);
        }
        fileURIStringBuffer.append(fileName);
        return fileURIStringBuffer.toString();
    }

    private void setDirectoryAsFirstRoot() {
        directory = new File("").getPath();
    }

    protected void createFile(String fileURI) throws IOException {
        //file = new File(URI.create(fileURI));
        int fileNameIndex = fileURI.lastIndexOf("\\");
        if (fileNameIndex == -1) {
            fileNameIndex = fileURI.lastIndexOf("/");
        }
        File folder = null;
        if (fileNameIndex > 0) {
            final String folderPath = fileURI.substring(0, fileNameIndex);
            folder = new File(folderPath);
            folder.mkdirs();
        }
        if (folder != null) {
            final String fileName = fileURI.substring(fileNameIndex + 1);
            file = new File(folder, fileName);
        } else {
            file = new File(fileURI);
        }
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    protected synchronized void openOutputStream() throws IOException {
        if (fileConnectionIsSet) {
            openStream(true);
            logOpen = true;
        }
    }

    private void openStream(boolean append) throws FileNotFoundException {
        if (fileConnectionIsSet) {
            outputStream = new FileOutputStream(file, append);
            logOpen = true;
        }
    }

    public synchronized void clear() {
        if (file != null) {
            try {
                if (outputStream != null) {
                    close();
                }
                openStream(false);
                if (!logOpen) {
                    close();
                }
            } catch (IOException e) {
                System.err.println("Failed to clear the log " + e);
            }
        }
    }

    public synchronized void close() throws IOException {
        if (logOpen) {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            outputStream = null;
            logOpen = false;
        }
    }

    public synchronized long getLogSize() {
        long logSize = SIZE_UNDEFINED;
        if (logOpen) {
            try {
                outputStream.flush();
                logSize = file.length();
            } catch (IOException e) {
                System.err.println("Failed to get the logsize " + e);
            }
        }
        return logSize;
    }

    synchronized void setFile(File file) {
        this.file = file;
        fileConnectionIsSet = this.file != null;
    }

    String[] addArrays(String[] a1, String[] a2) {
        String[] result = new String[a1.length + a2.length];
        System.arraycopy(a1, 0, result, 0, a1.length);
        System.arraycopy(a2, 0, result, a1.length, a2.length);
        return result;
    }

    public String[] getPropertyNames() {
        return addArrays(super.getPropertyNames(), MC_PROPERTY_NAMES);
    }

    public void setProperty(String name, String value)
            throws IllegalArgumentException {
        super.setProperty(name, value);

        if (name.equals(MCFileAppender.DIR_NAME_PROPERTY)) {
            setDirName(value);
        }
    }

    private void setDirName(String value) {
        directory = new File(Setup.get().getSettings().getLogFolder(), value).getPath();
    }
}