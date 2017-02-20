package com.magenta.mc.client.android.rpc;

import com.magenta.mc.client.android.http.HttpClient;
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
import java.util.Locale;
import java.util.TimeZone;

@SuppressWarnings("unused")
public class DefaultRpcResponseHandler implements RPCResponseHandler {

    private static final String BINARY_TRANSMISSION_DETAILS_METHOD = "binTransDetails";
    private static final String LOG_REQUEST_ERROR_METHOD = "logRequestError";
    private static final String IS_UPDATE_AVAILABLE = "isUpdateAvailable";
    private static final String BINARY_CHUNK_METHOD = "binChunk";

    private static final DateFormat UTC_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-d'T'HH:mm:ss'Z'", Locale.UK);

    static {
        UTC_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    protected synchronized static String formatDate(Date date) {
        return UTC_DATE_FORMAT.format(date);
    }

    public static void locations(String id, List<GeoLocation> locations) {
        HttpClient.getInstance().sendLocations(Long.valueOf(id), locations);
    }

    public static void locationsResponse(Long id) {
        Resender.getInstance().sent(GeoLocationBatch.METADATA, id);
    }

    public static void logRequestError(LogRequestError logRequestError) {
        JabberRPC.getInstance().call(
                LOG_REQUEST_ERROR_METHOD,
                new Object[]{
                        Long.valueOf(logRequestError.getRequestId()),
                        logRequestError.getMessage()
                },
                Long.parseLong(logRequestError.getId()));
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
        Long pieceId = Long.parseLong(chunk.getId());
        JabberRPC.getInstance().call(
                BINARY_CHUNK_METHOD,
                new Object[]{
                        Double.valueOf(pieceId),
                        chunk.getUri(),
                        Integer.valueOf(chunk.getOrderNumber()),
                        (chunk.isErrorOccured()) ? Boolean.TRUE : Boolean.FALSE,
                        Integer.valueOf(chunk.getErrorCode()),
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
                        Integer.valueOf(task.getCount())
                },
                (long) task.getId().hashCode());
    }

    public static void binTransDetails(RandomBinTransTask task) {
        JabberRPC.getInstance().call(
                BINARY_TRANSMISSION_DETAILS_METHOD,
                new Object[]{
                        task.getUri(),
                        Integer.valueOf(task.getCount())
                },
                (long) task.getId().hashCode());
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