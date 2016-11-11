package com.magenta.mc.client.log;

import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.setup.Setup;

import net.sf.microlog.core.Level;

import java.io.File;
import java.io.IOException;

/**
 * User: const
 * Date: 15.02.11
 * Time: 15:55
 * <p>
 * File appender with possibility to 'roll' if file grows > 'maxFileSize',
 * using number of backup files = 'maxBackupIndex'
 */
public class MCRollingFileAppender extends MCFileAppender {

    public static final String MAX_FILE_SIZE_PROPERTY = "maxFileSize";
    public static final String MAX_BACKUP_INDEX_PROPERTY = "maxBackupIndex";
    /**
     * The default maximum file size is 5MB.
     */
    protected long maxFileSize = 5 * 1024 * 1024;
    /**
     * There is one backup file by default.
     */
    protected int maxBackupIndex = 1;
    private long byteCount;
    private MCMemoryLogger memoryLogger = new MCMemoryLogger();
    private RecursionCheck recursionCheck = new RecursionCheck();
    private RecursionCheck memoryRecursionCheck = new RecursionCheck();

    public MCRollingFileAppender() {
        super();
    }

    /**
     * Implements the usual roll over behaviour.
     * <p>
     * thanks to Log4j's RollingFileAppender
     * <p>
     * <p>If <code>MaxBackupIndex</code> is positive, then files
     * {<code>File.1</code>, ..., <code>File.MaxBackupIndex -1</code>}
     * are renamed to {<code>File.2</code>, ...,
     * <code>File.MaxBackupIndex</code>}. Moreover, <code>File</code> is
     * renamed <code>File.1</code> and closed. A new <code>File</code> is
     * created to receive further log output.
     * <p>
     * <p>If <code>MaxBackupIndex</code> is equal to zero, then the
     * <code>File</code> is truncated with no backup files created.
     */
    public // synchronization not necessary since doAppend is alreasy synched
    void rollOver() {

        File target;
        File file;
        MCLogger logger = MCLoggerFactory.getLogger(getClass());
        logger.debug("rolling over; byteCount = " + byteCount + "; maxBackupIndex = " + maxBackupIndex);

        String fullFileName = directory + File.separator + fileName;

        // If maxBackups <= 0, then there is no file renaming to be done.
        if (maxBackupIndex > 0) {
            // Delete the oldest file, to keep Windows happy.
            file = new File(fullFileName + '.' + maxBackupIndex);
            if (file.exists())
                file.delete();

            // Map {(maxBackupIndex - 1), ..., 2, 1} to {maxBackupIndex, ..., 3, 2}
            for (int i = maxBackupIndex - 1; i >= 1; i--) {
                file = new File(fullFileName + "." + i);
                if (file.exists()) {
                    target = new File(fullFileName + '.' + (i + 1));
                    logger.trace("Renaming file " + file + " to " + target);
                    file.renameTo(target);
                }
            }

            // Rename fileName to fileName.1
            target = new File(fullFileName + "." + 1);

            try {
                close();// keep windows happy.
            } catch (IOException e) {
                logger.error("failed to close current file: " + fullFileName, e);
            }

            file = new File(fullFileName);
            logger.trace("Renaming file " + file + " to " + target);
            file.renameTo(target);
        }

        try {
            open();
        } catch (IOException e) {
            logger.error("setFile(" + fullFileName + ", false) call failed.", e);
            e.printStackTrace();
        }


    }

    public void open() throws IOException {
        super.open();
        byteCount = getLogSize();
    }

    public synchronized void doLog(final String clientID, final String name, final long time, final Level level, final Object message, final Throwable t) {
        if (recursionCheck.isRecursion()) {
            // ok we're now writing recursively, let's redirect this output to memory logger
            // until returned from recursion
            memoryLogger.get(name).log(level, message.toString(), t);
        } else {
            recursionCheck.execute(new Runnable() {
                public void run() {
                    if (logOpen && formatter != null) {
                        Settings settings = Setup.get().getSettings();
                        String logString = formatter.format(clientID,
                                name,
                                time,
                                level,
                                message + " server time: " + ServerTime.get(),
                                t);
                        try {
                            byte[] stringData = logString.getBytes();
                            outputStream.write(stringData);
                            if (lineSeparator == null) {
                                lineSeparator = DEFAULT_LINE_SEPARATOR;
                            }
                            outputStream.write(lineSeparator.getBytes());
                            outputStream.flush();

                            byteCount += stringData.length;
                        } catch (IOException e) {
                            System.err.println("Failed to log message " + e);
                        }

                        if (byteCount >= maxFileSize) {
                            rollOver();
                        }
                    }
                }
            });
            if (!memoryRecursionCheck.isRecursion()) {
                memoryRecursionCheck.execute(new Runnable() {
                    public void run() {
                        // we're back from recursive invocations,
                        // now write the messages from recursive logging to file
                        memoryLogger.flush();
                    }
                });
            }
        }
    }

    public String[] getPropertyNames() {
        return addArrays(super.getPropertyNames(), new String[]{MAX_FILE_SIZE_PROPERTY, MAX_BACKUP_INDEX_PROPERTY});
    }

    public void setProperty(String name, String value) throws IllegalArgumentException {
        super.setProperty(name, value);
        if (MAX_FILE_SIZE_PROPERTY.equals(name)) {
            maxFileSize = Math.round(Float.parseFloat(value) * 1024 * 1024);
        } else if (MAX_BACKUP_INDEX_PROPERTY.equals(name)) {
            maxBackupIndex = Integer.parseInt(value);
        }
    }
}
