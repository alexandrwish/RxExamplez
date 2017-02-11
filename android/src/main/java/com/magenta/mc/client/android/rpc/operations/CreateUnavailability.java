package com.magenta.mc.client.android.rpc.operations;

import com.magenta.mc.client.android.rpc.RPCOut;
import com.magenta.mc.client.components.waiting.LongOperationWithTimeout;

public class CreateUnavailability extends LongOperationWithTimeout {

    private static CreateUnavailability instance;
    private static String startDate;
    private static String endDate;
    private static String endAddress;
    private static String endPostcode;
    private static String reason;
    private static Callback callback;

    private CreateUnavailability(Runnable operation, long timeout) {
        super(operation, timeout);
    }

    public static CreateUnavailability getInstance() {
        if (instance == null) {
            instance = new CreateUnavailability(new Runnable() {
                public void run() {
                    RPCOut.createUnavailability(startDate, endDate, endAddress, endPostcode, reason);
                }
            }, 10000L);
        }
        return instance;
    }

    public static void create(final String startDate, final String endDate, final String endAddress, final String endPostcode, final String reason, final Callback callback) {
        CreateUnavailability.startDate = startDate;
        CreateUnavailability.endDate = endDate;
        CreateUnavailability.endAddress = endAddress;
        CreateUnavailability.endPostcode = endPostcode;
        CreateUnavailability.reason = reason;
        CreateUnavailability.callback = callback;
        getInstance().run();
    }

    public static void createDone() {
    }

    public static void createDone(final String errorMessage) {
        getInstance().done();
        if (callback != null) {
            callback.done(errorMessage);
        }
    }

    public interface Callback {
        void done(String errorMessage);
    }
}