package com.magenta.mc.client.android.rpc;

import com.magenta.hdmate.mx.model.OrderActionResult;
import com.magenta.mc.client.android.MobileApp;
import com.magenta.mc.client.android.http.HttpClient;
import com.magenta.mc.client.android.mc.client.resend.ResendableMetadata;
import com.magenta.mc.client.android.mc.client.resend.Resender;
import com.magenta.mc.client.android.mc.log.MCLogger;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.settings.Settings;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.mc.xml.XMLDataBlock;
import com.magenta.mc.client.android.renderer.Renderer;
import com.magenta.mc.client.android.rpc.operations.CreateUnavailability;
import com.magenta.mc.client.android.rpc.operations.JobsRefreshing;
import com.magenta.mc.client.android.service.ServicesRegistry;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import rx.Subscriber;
import rx.schedulers.Schedulers;

public class RPCOut extends DefaultRpcResponseHandler {


    public static final ResendableMetadata STATUS_RESENDABLE_METADATA = new ResendableMetadata("status");
    public static final MCLogger LOG = MCLoggerFactory.getLogger(RPCOut.class);
    // methods
    public static final String JOB_STATES_METHOD = "updateState";
    public static final String CHECK_IMEI = "checkImei";
    public static final String UPDATE_IMEI = "updateImei";
    public static final String RELOAD_JOBS = "reloadJobs";
    public static final String RELOAD_JOBS_HISTORY = "reloadJobsHistory";
    public static final String CREATE_UNAVAILABILITY = "createUnavailability";
    public static final String REQUEST_ACCOUNT_CONFIGURATION = "accountConfiguration";
    public static final String CLOSE_DRIVER_SHIFT = "closeDriverShift";
    public static final String USER_LOGGED = "processPerformerLogging";

    private static RPCOut instance;

    protected RPCOut() {
    }

    public static RPCOut getInstance() {
        if (instance == null) {
            instance = new RPCOut();
        }
        return instance;
    }

    public static void createUnavailability(String startDate, String endDate, String endAddress, String endPostcode, String reason) {
        JabberRPC.getInstance().call(CREATE_UNAVAILABILITY, new Object[]{startDate, endDate, endAddress, endPostcode, reason}, null);
    }

    public static void jobStates(final long id, String userId, String jobRef, String states, Map values) {
//        JabberRPC.getInstance().call(JOB_STATES_METHOD, new Object[]{jobRef, states, values, id}, id);
        HttpClient.getInstance().sendState(userId, jobRef, states, values)
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<OrderActionResult>>() {
                    public void onCompleted() {

                    }

                    public void onError(Throwable e) {
                        MCLoggerFactory.getLogger(RPCOut.class).error(e.getMessage(), e);
                    }

                    public void onNext(List<OrderActionResult> orderActionResults) {
                        Resender.getInstance().sent(STATUS_RESENDABLE_METADATA, id);
                    }
                });
    }

    public static void checkImei(String userId, String imei) {
        JabberRPC.getInstance().call(CHECK_IMEI, new Object[]{imei}, (long) (userId + imei).hashCode());
    }

    public static void updateImei(String userId, String imei) {
        JabberRPC.getInstance().call(UPDATE_IMEI, new Object[]{imei}, (long) (userId + imei).hashCode());
    }

    public static void reloadJobs() {
        JabberRPC.getInstance().call(RELOAD_JOBS, new Object[]{}, null);
    }

    public static void reloadJobsHistory() {
        JabberRPC.getInstance().call(RELOAD_JOBS_HISTORY, new Object[]{}, null);
    }

    public static void accountConfiguration() {
        JabberRPC.getInstance().call(REQUEST_ACCOUNT_CONFIGURATION, new Object[]{}, null);
    }

    public static void accountConfigurationResponse(Long id, XMLDataBlock data) {
        final XMLDataBlock stringBlock = data.getChildBlock("string");
        final XMLDataBlock response = stringBlock.getChildBlock("accountConfig");
        final XMLDataBlock account = response.getChildBlock("account");
        final XMLDataBlock config = response.getChildBlock("config");
        for (Enumeration e = config.getChildBlocks().elements(); e.hasMoreElements(); ) {
            XMLDataBlock child = (XMLDataBlock) e.nextElement();
            Settings.get().setProperty(child.getTagName(), child.getText());
        }
        Settings.get().saveSettings();
    }

    public static void createUnavailabilityResponse(final Long id, final XMLDataBlock data) {
        final XMLDataBlock stringBlock = data.getChildBlock("string");
        final String errorMessage;
        final XMLDataBlock errorsEl = stringBlock.getChildBlock("errors");
        if (errorsEl != null) {
            final StringBuilder sb = new StringBuilder();
            final Vector errors = errorsEl.getChildBlocks();
            for (final Object error : errors) {
                sb.append(((XMLDataBlock) error).getText()).append("\n");
            }
            errorMessage = sb.toString();
        } else {
            errorMessage = null;
        }
        CreateUnavailability.createDone(errorMessage);
    }

    public static void closeDriverShift() {
        JabberRPC.getInstance().call(CLOSE_DRIVER_SHIFT, new Object[]{}, null);
    }

    public static void sendImei(String imei) {
        JabberRPC.getInstance().call(USER_LOGGED, new Object[]{imei}, System.currentTimeMillis());
    }

    @SuppressWarnings("unchecked")
    public void updateStateResponse(Long id, XMLDataBlock data) {
        final XMLDataBlock stringBlock = data.getChildBlock("string");
        final XMLDataBlock response = stringBlock.getChildBlock("jobChangeStatusResponse");
        boolean sent = true;
        final XMLDataBlock errorsEl = response.getChildBlock("errors");
        if (errorsEl != null) {
            final XMLDataBlock isStaticErrorEl = response.getChildBlock("isStaticError");
            if (isStaticErrorEl == null || !Boolean.valueOf(isStaticErrorEl.getText())) {
                // is recoverable error, resend
                sent = false;
            }
            MCLoggerFactory.getLogger(getClass()).debug("change job status failed id:" + id);
            final StringBuilder sb = new StringBuilder();
            final Vector errors = errorsEl.getChildBlocks();
            for (Object error : errors) {
                sb.append(((XMLDataBlock) error).getText()).append("\n");
            }
            Setup.get().getUI().getDialogManager().messageSafe(MobileApp.localize("Error"), sb.toString());
        } else {
            MCLoggerFactory.getLogger(getClass()).debug("change job status ok id:" + id);
        }
        if (sent) {
            Resender.getInstance().sent(STATUS_RESENDABLE_METADATA, id);
        }
        final XMLDataBlock jobUpdateEl = response.getChildBlock("jobUpdate");
        if (jobUpdateEl != null) {
            ServicesRegistry.getDataController().updateJob(Renderer.renderJob(jobUpdateEl), true);
        }
    }

    @SuppressWarnings("unchecked")
    public void checkImeiResponse(Long id, XMLDataBlock data) {
        final XMLDataBlock stringBlock = data.getChildBlock("string");
        final XMLDataBlock response = stringBlock.getChildBlock("checkImeiResponse");
        final String status = response.getChildBlockText("status");
        if (status.equalsIgnoreCase("ok")) {
            // ok, the imei is the same
            System.out.println("imei verification ok");
        } else if (status.equalsIgnoreCase("refresh")) {
            System.out.println("imei has been changed, refresh jobs");
            List jobs = Renderer.parseJobs(response.getChildBlock("jobs"));
            ServicesRegistry.getDataController().reloadJobs(jobs);
        } else {
            throw new IllegalArgumentException("Unexpected status while checking imei: " + status);
        }
    }

    @SuppressWarnings("unchecked")
    public void reloadJobsResponse(Long id, XMLDataBlock data) {
        final XMLDataBlock stringBlock = data.getChildBlock("string");
        final XMLDataBlock response = stringBlock.getChildBlock("reloadJobsResponse");
        XMLDataBlock jobsBlock = response.getChildBlock("jobs");
        if (jobsBlock != null) {
            System.out.println("parsing jobs for reload");
            List jobs = Renderer.parseJobs(jobsBlock);
            ServicesRegistry.getDataController().reloadJobs(jobs);
            MobileApp.runTask(new Runnable() {
                public void run() {
                    JobsRefreshing.refreshDone();
                }
            });
        } else {
            throw new IllegalArgumentException("Reload jobs return nothing");
        }
    }
}