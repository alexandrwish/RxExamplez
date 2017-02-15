package com.magenta.mc.client.android.rpc;

import com.magenta.mc.client.android.mc.client.resend.Resender;
import com.magenta.mc.client.android.mc.log_sending.LogRequestError;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.mc.tracking.GeoLocation;
import com.magenta.mc.client.android.mc.tracking.GeoLocationBatch;
import com.magenta.mc.client.android.mc.util.Strconv;
import com.magenta.mc.client.android.rpc.bin_chunks.BinaryChunkResendable;
import com.magenta.mc.client.android.rpc.bin_chunks.BinaryTransmissionTask;
import com.magenta.mc.client.android.rpc.bin_chunks.random.RandomBinTransTask;
import com.magenta.mc.client.android.rpc.bin_chunks.random.RandomBinaryTransmitter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DefaultRpcResponseHandler implements RPCResponseHandler {

    public static final String LOCATIONS_METHOD = "locations";
    public static final String LOG_REQUEST_ERROR_METHOD = "logRequestError";
    public static final String IS_UPDATE_AVAILABLE = "isUpdateAvailable";
    public static final String BINARY_CHUNK_METHOD = "binChunk";
    public static final String BINARY_TRANSMISSION_DETAILS_METHOD = "binTransDetails";

    private static final DateFormat UTC_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-d'T'HH:mm:ss'Z'");

    static {
        UTC_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    protected synchronized static String formatDate(Date date) {
        return UTC_DATE_FORMAT.format(date);
    }

    public static void locations(String id, List locations) {
        if (locations == null || locations.isEmpty()) {
            return;
        }
        Object[][] locationsArray = new Object[locations.size()][8];
        for (int i = 0; i < locationsArray.length; i++) {
            Object[] locationArray = new Object[10];
            GeoLocation location = (GeoLocation) locations.get(i);
            locationArray[0] = Setup.get().getPlatformUtil().getImei();
            locationArray[1] = location.getUserId();
            locationArray[2] = location.getState();
            locationArray[3] = location.getLat();
            locationArray[4] = location.getLon();
            locationArray[5] = new Date(location.getRetrieveTimestamp().longValue());
            locationArray[6] = location.getSpeed();
            locationArray[7] = location.getHeading();
            locationArray[8] = location.getSatelliteCount();
            locationArray[9] = location.getSource();
            locationsArray[i] = locationArray;
        }
        String trackerComponentName = Setup.get().getSettings().getProperty("tracker.component");
        if (trackerComponentName != null) {
            String trackerJid = trackerComponentName + "." + Setup.get().getSettings().getServerName();
            JabberRPC.getInstance().call(trackerJid, LOCATIONS_METHOD, new Object[]{locationsArray}, new Long(Long.parseLong(id)));
        } else {
            JabberRPC.getInstance().call(LOCATIONS_METHOD, new Object[]{locationsArray}, new Long(Long.parseLong(id)));
        }
    }

    public static void locationsResponse(Long id) {
        Resender.getInstance().sent(GeoLocationBatch.METADATA, id);
    }

    public static void logRequestError(LogRequestError logRequestError) {
        JabberRPC.getInstance().call(
                LOG_REQUEST_ERROR_METHOD,
                new Object[]{
                        new Long(logRequestError.getRequestId()),
                        logRequestError.getMessage()
                },
                new Long(Long.parseLong(logRequestError.getId())));
    }

    public static void logRequestErrorResponse(Long id) {
        Resender.getInstance().sent(LogRequestError.METADATA, id);
    }

    public static void isUpdateAvailable(String currentVersion, String platform, String application) {
        JabberRPC.getInstance().call(IS_UPDATE_AVAILABLE, new Object[]{currentVersion, platform, application}, null);
    }

    public static void isUpdateAvailableResponse(Long id, Boolean available) {
        Setup.get().getUpdateCheck().complete(available);
    }

    public static void binChunk(BinaryChunkResendable chunk) {
        Long pieceId = new Long(Long.parseLong(chunk.getId()));
        JabberRPC.getInstance().call(
                BINARY_CHUNK_METHOD,
                new Object[]{
                        new Double(pieceId.longValue()),
                        chunk.getUri(),
                        new Integer(chunk.getOrderNumber()),
                        (chunk.isErrorOccured()) ? Boolean.TRUE : Boolean.FALSE,
                        new Integer(chunk.getErrorCode()),
                        Strconv.toBase64(chunk.getData(), chunk.getData().length)
                },
                pieceId);
    }

    public static void binChunkResponse(Long id, Object[] response) {
        Resender.getInstance().sent(RandomBinTransTask.RESENDABLE_METADATA, id, response);
    }

    public static void binTransDetails(BinaryTransmissionTask task) {
        JabberRPC.getInstance().call(
                BINARY_TRANSMISSION_DETAILS_METHOD,
                new Object[]{
                        task.getUri(),
                        new Integer(task.getCount())
                },
                new Long(task.getId().hashCode()));
    }

    public static void binTransDetails(RandomBinTransTask task) {
        JabberRPC.getInstance().call(
                BINARY_TRANSMISSION_DETAILS_METHOD,
                new Object[]{
                        task.getUri(),
                        new Integer(task.getCount())
                },
                new Long(task.getId().hashCode()));
    }

    public static void binTransDetailsResponse(Long id, String uri) {
        RandomBinaryTransmitter.transDetailsReceived(uri);
    }

    public boolean handleError(String id) {
        System.out.println("RPC error received: " + id); // not handling errors currently, just recending over time

        final Object[] methodNameAndId = JabberRPC.decomposeId(id);
        final String methodName = (String) methodNameAndId[0];
        final Long responseId = (Long) methodNameAndId[1];

        if ("locations".equals(methodName)) {
            Resender.getInstance().error(GeoLocationBatch.METADATA, responseId);
            return true;
        } else if ("logRequestError".equals(methodName)) {
            Resender.getInstance().error(LogRequestError.METADATA, responseId);
            return true;
        } else if ("binChunk".equals(methodName)) {
            Resender.getInstance().error(BinaryChunkResendable.METADATA, responseId);
            return true;
        } else if ("binTransDetails".equals(methodName)) {
            Resender.getInstance().error(RandomBinTransTask.RESENDABLE_METADATA, responseId);
            return true;
        }

        return false;
    }
}