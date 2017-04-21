package com.magenta.mc.client.android.log;

import net.sf.microlog.core.Level;

import java.io.File;
import java.io.IOException;

public class MCRollingFileAppender extends MCFileAppender {

    public static final String MAX_FILE_SIZE_PROPERTY = "maxFileSize";
    public static final String MAX_BACKUP_INDEX_PROPERTY = "maxBackupIndex";
    private long maxFileSize = 5242880L;
    private int maxBackupIndex = 1;
    private long byteCount;
    private MCMemoryLogger memoryLogger = new MCMemoryLogger();
    private RecursionCheck recursionCheck = new RecursionCheck();
    private RecursionCheck memoryRecursionCheck = new RecursionCheck();

    public MCRollingFileAppender() {
    }

    public void rollOver() {
        MCLogger logger = MCLoggerFactory.getLogger(this.getClass());
        logger.debug("rolling over; byteCount = " + this.byteCount + "; maxBackupIndex = " + this.maxBackupIndex);
        String fullFileName = this.directory + File.separator + this.fileName;
        if (this.maxBackupIndex > 0) {
            File file = new File(fullFileName + '.' + this.maxBackupIndex);
            if (file.exists()) {
                file.delete();
            }
            File target;
            for (int i = this.maxBackupIndex - 1; i >= 1; --i) {
                file = new File(fullFileName + "." + i);
                if (file.exists()) {
                    target = new File(fullFileName + '.' + (i + 1));
                    logger.trace("Renaming file " + file + " to " + target);
                    file.renameTo(target);
                }
            }
            target = new File(fullFileName + "." + 1);
            try {
                this.close();
            } catch (IOException var7) {
                logger.error("failed to close current file: " + fullFileName, var7);
            }
            file = new File(fullFileName);
            logger.trace("Renaming file " + file + " to " + target);
            file.renameTo(target);
        }
        try {
            this.open();
        } catch (IOException var6) {
            logger.error("setFile(" + fullFileName + ", false) call failed.", var6);
            var6.printStackTrace();
        }
    }

    public void open() throws IOException {
        super.open();
        this.byteCount = this.getLogSize();
    }

    public synchronized void doLog(final String clientID, final String name, final long time, final Level level, final Object message, final Throwable t) {
        if (this.recursionCheck.isRecursion()) {
            this.memoryLogger.get(name).log(level, message.toString(), t);
        } else {
            this.recursionCheck.execute(new Runnable() {
                public void run() {
                    if (MCRollingFileAppender.this.logOpen && MCRollingFileAppender.this.formatter != null) {
                        String logString = MCRollingFileAppender.this.formatter.format(clientID, name, time, level, message, t);
                        try {
                            byte[] stringData = logString.getBytes();
                            MCRollingFileAppender.this.outputStream.write(stringData);
                            if (MCRollingFileAppender.this.lineSeparator == null) {
                                MCRollingFileAppender.this.lineSeparator = "\r\n";
                            }
                            MCRollingFileAppender.this.outputStream.write(MCRollingFileAppender.this.lineSeparator.getBytes());
                            MCRollingFileAppender.this.outputStream.flush();
                            MCRollingFileAppender.this.byteCount = MCRollingFileAppender.this.byteCount + (long) stringData.length;
                        } catch (IOException var4) {
                            System.err.println("Failed to log message " + var4);
                        }

                        if (MCRollingFileAppender.this.byteCount >= MCRollingFileAppender.this.maxFileSize) {
                            MCRollingFileAppender.this.rollOver();
                        }
                    }

                }
            });
            if (!this.memoryRecursionCheck.isRecursion()) {
                this.memoryRecursionCheck.execute(new Runnable() {
                    public void run() {
                        MCRollingFileAppender.this.memoryLogger.flush();
                    }
                });
            }
        }
    }

    public String[] getPropertyNames() {
        return this.addArrays(super.getPropertyNames(), new String[]{"maxFileSize", "maxBackupIndex"});
    }

    public void setProperty(String name, String value) throws IllegalArgumentException {
        super.setProperty(name, value);
        if ("maxFileSize".equals(name)) {
            this.maxFileSize = (long) Math.round(Float.parseFloat(value) * 1024.0F * 1024.0F);
        } else if ("maxBackupIndex".equals(name)) {
            this.maxBackupIndex = Integer.parseInt(value);
        }

    }
}