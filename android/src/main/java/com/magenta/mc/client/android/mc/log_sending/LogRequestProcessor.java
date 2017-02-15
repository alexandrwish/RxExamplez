package com.magenta.mc.client.android.mc.log_sending;

import com.magenta.mc.client.android.mc.client.resend.Resender;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.mc.storage.BinaryStorable;
import com.magenta.mc.client.android.mc.storage.StorableMetadata;
import com.magenta.mc.client.android.mc.storage.Storage;
import com.magenta.mc.client.android.rpc.bin_chunks.random.RandomBinaryTransmitter;

import net.sf.microproperties.Properties;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import EDU.oswego.cs.dl.util.concurrent.Mutex;

/**
 * @author Petr Popov
 *         Created: 23.01.12 17:06
 */
public class LogRequestProcessor {

    private static final long LOG_REQUEST_EXPIRATION_2_DAYS = 2 * 24 * 60 * 60 * 1000;
    public static StorableMetadata LOG_BLOB_METADATA = new StorableMetadata("log_blobs", true);
    private static String DATE_FORMAT_STR = "yyyy-MM-dd HH:mm:ss,SSS";
    private static int DATE_FORMAT_STR_LEN = DATE_FORMAT_STR.length();
    private static Mutex requestProcessMutex = new Mutex();
    private static Map runningRequests = new HashMap();
    private final DecimalFormat pieceIndexFormat = new DecimalFormat("000000000"); // nine-digit format - enough to store at least 100Mb of log pieces
    private final int pieceMaxLength;
    private final String LOG_URI_LOCATION = "app_logs";
    private SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_STR);

    public LogRequestProcessor() {
        pieceMaxLength = Setup.get().getSettings().getIntProperty("log.sending.piece.length", "50000");
    }

    private static boolean acquireRequestRun(Long requestId) {
        try {
            requestProcessMutex.acquire();
            if (runningRequests.containsKey(requestId)) {
                MCLoggerFactory.getLogger(LogRequestProcessor.class).debug("log request in progress, permission denied: " + requestId);
                return false;
            } else {
                runningRequests.put(requestId, Boolean.TRUE);
                MCLoggerFactory.getLogger(LogRequestProcessor.class).debug("log request permission granted: " + requestId);
                return true;
            }
        } catch (InterruptedException e) {
            MCLoggerFactory.getLogger(LogRequestProcessor.class).debug("acquireRequestRun interrupted, skipping");
            return false;
        } finally {
            requestProcessMutex.release();
        }
    }

    private static boolean releaseRequest(Long requestId) {
        try {
            requestProcessMutex.acquire();
            return runningRequests.remove(requestId) != null;
        } catch (InterruptedException e) {
            return false;
        } finally {
            MCLoggerFactory.getLogger(LogRequestProcessor.class).debug("log request permission released: " + requestId);
            requestProcessMutex.release();
        }
    }

    public static void requestDetailsReceived(Long requestId) {
        Storage storage = Setup.get().getStorage();
        LogRequest request = (LogRequest) storage.load(LogRequest.STORABLE_METADATA, requestId.toString());
        if (request == null) {
            MCLoggerFactory.getLogger(LogRequestProcessor.class).warn("Signalled log request not found upon confirmation: " + requestId);
            return;
        }
        request.setState(LogRequest.STATE_SIGNALLED);
        request.setDateChanged(Setup.get().getSettings().getCurrentDate());
        storage.save(request);
    }

    public void process(final LogRequest request) {
        process(request, true);
    }

    private void process(final LogRequest request, final boolean syncRequired) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    walk(request, syncRequired);
                } catch (Exception e) {
                    MCLoggerFactory.getLogger(getClass()).error(e);
                    handleRequestError(e, request);
                }
            }
        }).start();
    }

    private void handleRequestError(Exception e, LogRequest request) {
        int index = request.getCount();
        if (index == -1) { //if this is first piece for request
            index = 0;
        }
        request.setCount(index + 1);

        StringWriter traceWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(traceWriter));
        String errorMsg = e.getMessage() + "\n" + traceWriter.toString();

        LogRequestError logRequestError = new LogRequestError(request.getRequestId(), errorMsg);
        Resender.getInstance().send(logRequestError);

        finalizeRequest(request);
    }

    public void checkRequests() {
        try {
            Storage storage = Setup.get().getStorage();
            List storedRequests = storage.load(LogRequest.STORABLE_METADATA);
            for (int i = 0; i < storedRequests.size(); i++) {
                LogRequest request = (LogRequest) storedRequests.get(i);
                switch (request.getState()) {
                    case LogRequest.STATE_RECEIVED:
                        try {
                            process(request, false);
                        } catch (Exception e) {
                            MCLoggerFactory.getLogger(getClass()).error(e);
                            handleRequestError(e, request);
                        }
                        break;
                    case LogRequest.STATE_DONE:
                        // request is processed, but count signal was not confirmed - signal piece count
                        BinaryStorable logBlob = Setup.get().getStorage().getBinary(LOG_BLOB_METADATA, request.getId());
                        transmitLogBinary(request, logBlob);
                        break;
                    case LogRequest.STATE_SIGNALLED:
                        // this is an old accomplished request, just check and
                        // delete requests older than 2 days
                        long elapsedMillis = Setup.get().getSettings().getCurrentDate().getTime() - request.getDateChanged().getTime();
                        if (elapsedMillis > LOG_REQUEST_EXPIRATION_2_DAYS) {
                            storage.delete(request);
                        }
                        break;
                }
            }
        } catch (Exception e) {
            MCLoggerFactory.getLogger(getClass()).error("Unexpected error while checking log requests", e);
        }

    }

    private void walk(LogRequest request) throws IOException {
        walk(request, true);
    }

    private void walk(LogRequest request, boolean syncRequired) throws IOException {
        if (request.getType() != LogType.APP) {
            throw new RuntimeException("Only APP log type supported");
        }
        Properties loggerProperties = MCLoggerFactory.getInstance().getProperties();
        if (loggerProperties.getProperty("microlog.appender.MCRollingFileAppender") == null) {
            throw new RuntimeException("microlog.appender.MCRollingFileAppender not defined, other appenders not supported");
        }
        String formatterPattern = loggerProperties.getProperty("microlog.appender.MCRollingFileAppender.formatter.pattern");
        if (formatterPattern == null || !formatterPattern.startsWith("%d{ISO8601}")) {
            throw new RuntimeException("unsupported formatter pattern: " + formatterPattern + ", only %d{ISO8601} is supported currently");
        }
        String filename = loggerProperties.getProperty("microlog.appender.MCRollingFileAppender.filename", "microlog.txt");
        String directory = loggerProperties.getProperty("microlog.appender.MCRollingFileAppender.directory", "");
        String maxBackupIndex = loggerProperties.getProperty("microlog.appender.MCRollingFileAppender.maxBackupIndex");
        int availableBackupFiles = maxBackupIndex != null && maxBackupIndex.trim().length() > 0
                ? Integer.parseInt(maxBackupIndex.trim())
                : 0;

        StringBuffer buffer = new StringBuffer(pieceMaxLength);
        long startDate = request.getStartDate().getTime();
        long endDate = request.getEndDate().getTime();
        String logLine;

        boolean complete = false;
        boolean alreadyInInterval = false;

        Long requestId = new Long(request.getRequestId());
        if (acquireRequestRun(requestId)) {
            OutputStream logBlobStream = null;
            BinaryStorable logBlob = Setup.get().getStorage().getBinary(LOG_BLOB_METADATA, requestId.toString());
            try {
                while (!complete && availableBackupFiles > -1) {
                    File dir = new File(Setup.get().getSettings().getLogFolder(), directory);
                    File file = new File(dir, filename + ((availableBackupFiles > 0) ? ("." + availableBackupFiles) : ""));

                    if (file.exists()) {
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        while ((logLine = reader.readLine()) != null) {

                            if (logLine.length() > DATE_FORMAT_STR_LEN) {
                                try {
                                    long date = format.parse(logLine.substring(0, DATE_FORMAT_STR_LEN)).getTime();
                                    if (date < startDate) {
                                        continue;
                                    }
                                    if (date > startDate && date < endDate) {
                                        alreadyInInterval = true;
                                        if (logBlobStream == null) {
                                            logBlob.setData(new byte[0]);
                                            logBlob.save();
                                            logBlobStream = logBlob.getOutputStream();
                                        }
                                    }
                                    if (date > endDate) {
                                        complete = true;
                                    }
                                } catch (ParseException e) {
                                    //include line if it in interval
                                }
                            }

                            if (complete) {
                                if (buffer.length() > 0) {
                                    logBlobStream.write(buffer.toString().getBytes());
                                    buffer = new StringBuffer();
                                }
                                break;
                            } else if (alreadyInInterval) {
                                if (buffer.length() + logLine.length() >= pieceMaxLength) {
                                    logBlobStream.write(buffer.toString().getBytes());
                                    buffer = new StringBuffer();
                                    sleep();
                                }

                                buffer.append(logLine)
                                        .append('\n');
                            }

                        }
                    }

                    availableBackupFiles--;
                }
                if (!complete && buffer.length() > 0) {
                    logBlobStream.write(buffer.toString().getBytes());
                }

                finalizeRequest(request);
            } finally {
                releaseRequest(requestId);
                boolean empty = false;
                if (logBlobStream != null) {
                    try {
                        logBlobStream.close();
                    } catch (Exception e) {
                        // beat it
                    }
                    empty = logBlob.length() == 0;
                } else {
                    empty = true;
                }
                if (empty) {
                    logBlob.setData(new byte[0]);
                    logBlob.save();
                    logBlobStream = logBlob.getOutputStream();
                    logBlobStream.write("No data found for given period".getBytes());
                    logBlobStream.close();
                }
            }

            transmitLogBinary(request, logBlob);

            // time to cleanup
            System.gc();
        }
    }

    private void sleep() {
        // give a little breathe to the system and GC
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // fall through
        }
    }

    private void transmitLogBinary(LogRequest request, BinaryStorable logBlob) {
        RandomBinaryTransmitter.transmit(LOG_URI_LOCATION + "/" + request.getId(), logBlob.getUri(), true);
        request.setState(LogRequest.STATE_SIGNALLED);
        request.setDateChanged(Setup.get().getSettings().getCurrentDate());
        Setup.get().getStorage().save(request);
    }

    private void finalizeRequest(LogRequest request) {
        request.setState(LogRequest.STATE_DONE);
        request.setDateChanged(Setup.get().getSettings().getCurrentDate());
        Setup.get().getStorage().save(request);
    }

}
