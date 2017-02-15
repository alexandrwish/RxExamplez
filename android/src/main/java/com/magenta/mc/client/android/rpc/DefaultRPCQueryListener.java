package com.magenta.mc.client.android.rpc;

import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.log_sending.LogRequest;
import com.magenta.mc.client.android.mc.log_sending.LogRequestProcessor;
import com.magenta.mc.client.android.mc.log_sending.LogType;
import com.magenta.mc.client.android.mc.setup.Setup;

import java.util.Date;

public class DefaultRPCQueryListener implements RPCQueryListener {

    public void logRequest(Double requestId, Date startDate, Date endDate, String type) {
        try {
            LogRequest request = new LogRequest(requestId.longValue(), startDate, endDate, LogType.valueOf(type));
            LogRequest existingRequest = (LogRequest) Setup.get().getStorage().load(LogRequest.STORABLE_METADATA, request.getId());
            if (existingRequest == null) {
                Setup.get().getStorage().save(request);
                new LogRequestProcessor().process(request);
            } else {
                MCLoggerFactory.getLogger(getClass()).debug("Duplicate log request, ignoring: " + request.getId());
            }
        } catch (Exception e) {
            MCLoggerFactory.getLogger(getClass()).error("Error while parsing log request", e);
        }
    }

    public void updateAvailable(String platform, String application) {
        Setup.get().getUpdateCheck().updateReported(platform, application);
    }
}