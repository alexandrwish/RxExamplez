package com.magenta.maxunits.mobile.dlib.receiver;

import android.net.Uri;
import android.util.Pair;

import com.google.gson.Gson;
import com.magenta.maxunits.mobile.dlib.DistributionApplication;
import com.magenta.maxunits.mobile.dlib.db.dao.DistributionDAO;
import com.magenta.maxunits.mobile.dlib.xmpp.XMPPStream2;
import com.magenta.maxunits.mobile.entity.LocationEntity;
import com.magenta.maxunits.mobile.mc.MxAndroidUtil;
import com.magenta.maxunits.mobile.mc.MxSettings;
import com.magenta.maxunits.mobile.service.ServicesRegistry;
import com.magenta.maxunits.mobile.utils.UserUtils;
import com.magenta.mc.client.client.Login;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.setup.Setup;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginCheckReceiver extends TimerTask implements Runnable {

    private XMPPStream2.KeepAliveTask2 keepAliveTask;
    private OkHttpClient httpClient;

    public static String generateLoginURL(String driver) {
        Uri.Builder builder = Uri.parse("http://" + Setup.get().getSettings().get(MxSettings.API_ADDRESS)).buildUpon();
        for (String s : ((String) Setup.get().getSettings().get(MxSettings.API_PATH)).split("/")) {
            builder.appendPath(s);
        }
        Pair<String, String> name = UserUtils.splitName(driver);
        builder.appendPath("getPerformerValidDate")
                .appendQueryParameter("account", name.second)
                .appendQueryParameter("login", name.first)
                .appendQueryParameter("imei", MxAndroidUtil.getImei());
        return builder.toString();
    }

    public void run(XMPPStream2.KeepAliveTask2 keepAliveTask, OkHttpClient client) {
        this.keepAliveTask = keepAliveTask;
        this.httpClient = client;
        run();
    }

    public void run() {
        List<String> drivers = DistributionDAO.getInstance(DistributionApplication.getContext()).getDrivers();
        checkDriverAndSentLocations(httpClient, UserUtils.cutComponentName(Settings.get().getUserId()), true);
        for (String driver : drivers) {
            checkDriverAndSentLocations(httpClient, driver, false);
        }
        cancel();
    }

    public void checkDriverAndSentLocations(OkHttpClient httpClient, String driver, boolean withKeepAlive) {
        try {
            if (!driver.isEmpty()) {
                Response response = httpClient.newCall(new Request.Builder().url(generateLoginURL(driver)).get().build()).execute();
                String result = response.body().string();
                if (result.trim().isEmpty()) {
                    if (withKeepAlive) {
                        keepAliveTask.tryToSend();
                    }
                } else {
                    try {
                        final long validDate = Long.valueOf(result);
                        DistributionDAO.getInstance(DistributionApplication.getContext()).clearLocationsAfter(validDate, driver);
                        if (withKeepAlive) {
                            Login.getInstance().logout(true);
                            ServicesRegistry.getWorkflowService().logout();
                        }
                    } catch (NumberFormatException | SQLException e) {
                        MCLoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
                        if (withKeepAlive) {
                            keepAliveTask.tryToSend();
                        }
                    }
                }
                sendLocations(httpClient, driver);
            }
        } catch (Exception e) {
            MCLoggerFactory.getLogger(getClass()).error(e.getStackTrace());
        }
    }

    public void sendLocations(OkHttpClient client, String driver) throws IOException, SQLException {
        List<LocationEntity> geoLocations = DistributionDAO.getInstance(DistributionApplication.getContext()).getGeoLocations(driver);
        int locationsCount = geoLocations.size();
        if (locationsCount == 0) {
            return;
        }
        StringBuilder sb = new StringBuilder("[");
        long max = 0;
        Gson gson = new Gson();
        for (LocationEntity location : geoLocations) {
            sb.append(gson.toJson(location.toRecord())).append(",");
            max = max > location.getId() ? max : location.getId();
        }
        sb.append("]");
        Request request = new Request.Builder().url(generateLocationURL()).method("POST", RequestBody.create(null, sb.toString().replace(",]", "]"))).addHeader("Content-Type", "application/json; charset=utf-8").build();
        MCLoggerFactory.getLogger(getClass()).info("sending " + locationsCount + " locations...");
        if (client.newCall(request).execute().body().string().trim().isEmpty()) {
            DistributionDAO.getInstance(DistributionApplication.getContext()).clearLocations(max, driver);
            MCLoggerFactory.getLogger(getClass()).info("locations sent!");
        } else {
            MCLoggerFactory.getLogger(getClass()).info("locations not sent!");
        }
    }

    private String generateLocationURL() {
        return "http://" + Setup.get().getSettings().get(MxSettings.API_ADDRESS) + Setup.get().getSettings().get(MxSettings.API_PATH) + "/saveLocations";
    }
}