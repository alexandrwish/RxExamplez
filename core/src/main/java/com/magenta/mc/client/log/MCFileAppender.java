package com.magenta.mc.client.log;

import com.magenta.mc.client.setup.Setup;

import net.sf.microlog.core.appender.AbstractFileAppender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created 11.11.2010
 *
 * @author Konstantin Pestrikov
 *         <p>
 *         The file name can be passed with the property
 *         <code>microlog.appender.FileAppender.filename</code>.
 *         <p>
 *         The directory can be passed with the property
 *         <code>microlog.appender.FileAppender.directory</code>
 *         <p>
 *         The directory is possible to set with the <code>setDirectory()</code> method.
 *         If this is not set the default directory is used. The default directory is
 *         fetched by calling <code>FileSystemRegistry.listRoots()</code>, where the
 *         first root is used.
 */
public class MCFileAppender extends AbstractFileAppender {

    /**
     * The protocol to be used for opening a <code>FileConnection</code> object.
     */
    public static final String FILE_PROTOCOL = "file:///";

    public static final String DIR_NAME_PROPERTY = "directory";

    public static final String[] MC_PROPERTY_NAMES = {DIR_NAME_PROPERTY};

    /**
     * The <code>FileConnection</code> for accessing the log file.
     */
    protected File file;

    public MCFileAppender() {
        super();
    }

    /**
     * Create the <code>fileURI</code> to be used as a log file.
     *
     * @return the <code>fileURI</code>.
     */
    protected String createFileURI() {
        StringBuffer fileURIStringBuffer = new StringBuffer(
                DEFAULT_STRING_BUFFER_SIZE);
        //fileURIStringBuffer.append(FILE_PROTOCOL);

        boolean fileNameContainsPath = (fileName.indexOf('/') != -1)
                || (fileName.indexOf('\\') != -1);

        if (!fileNameContainsPath && directory == null) {
            setDirectoryAsFirstRoot();
        }

        if (directory != null) {
            fileURIStringBuffer.append(directory).append(File.separator);
        }

        fileURIStringBuffer.append(fileName);

        return fileURIStringBuffer.toString();
    }

    /**
     * Set the <code>directory</code> member variable to the first directory
     * found by <code>FileSystemRegistry.listRoots()</code>.
     */
    private void setDirectoryAsFirstRoot() {
        directory = new File("").getPath();
    }

    /**
     * Create the file from the specified <code>fileURI</code>. If the file
     * already exists, no file is created.
     *
     * @param fileURI the <code>fileURI</code> to use for creation.
     * @throws IOException if the creation fails.
     */
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

        //System.out.println("The created file is " + file.getPath());
    }

    /**
     * Open the <code>OutputStream</code> for the created file. The member
     * variable <code>outputStream</code> shall be set after this method has
     * been called.
     *
     * @throws IOException
     */
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

    /**
     * @see net.sf.microlog.core.appender.AbstractAppender#clear()
     */
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
                //file.truncate(0);
            } catch (IOException e) {
                System.err.println("Failed to clear the log " + e);
            }
        }
    }

    /**
     * @see net.sf.microlog.core.appender.AbstractAppender#close()
     */
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

    /**
     * Get the size of the log. This is equivalent of calling
     * <code>fileSize()</code> on the created <code>FileConnection</code>.
     *
     * @return the size of the log.
     */
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

    /**
     * Get the total size. The total size is fetched by calling
     * <code>totalSize()</code> on the created <code>FileConnection</code>.
     *
     * @return the total size of the file system the connection's target resides
     * on.
     */
    public synchronized long totalSize() {
        long totalSize = SIZE_UNDEFINED;

        if (logOpen) {
            try {
                outputStream.flush();
                totalSize = file.length();
            } catch (IOException e) {
                System.err.println("Failed to get the total size." + e);
            }
        }

        return totalSize;
    }

    /**
     * Get the used size. The total size is fetched by calling
     * <code>usedSize()</code> on the created <code>FileConnection</code>.
     *
     * @return Determines the used memory of a file system the connection's
     * target resides on. This may only be an estimate and may vary
     * based on platform-specific file system blocking and metadata
     * information.
     */
    public synchronized long usedSize() {
        long usedSize = SIZE_UNDEFINED;

        if (logOpen) {
            try {
                outputStream.flush();
                usedSize = file.length();
            } catch (IOException e) {
                System.err.println("Failed to get the total size. " + e);
            }
        }

        return usedSize;
    }

    /**
     * Get the URL of the file that is opened, i.e. a call is made to
     * <code>getURL()</code> on the opened <code>FileConnection</code>.
     *
     * @return the URL of the opened connection. If no connection is opened, an
     * empty <code>String</code> is returned.
     */
    public synchronized String getURL() {
        String url = "";

        if (file != null) {
            try {
                url = file.toURI().toURL().toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return url;
    }

    /**
     * @param file the fileConnection to set
     */
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

    protected void setDirName(String value) {
        directory = new File(Setup.get().getSettings().getLogFolder(), value).getPath();
    }

}
