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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.TimerTask;

public class LoginCheckReceiver extends TimerTask implements Runnable {

    private XMPPStream2.KeepAliveTask2 keepAliveTask;
    private HttpClient httpClient;

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

    public void run(XMPPStream2.KeepAliveTask2 keepAliveTask, HttpClient client) {
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

    public void checkDriverAndSentLocations(HttpClient httpClient, String driver, boolean withKeepAlive) {
        try {
            if (!driver.isEmpty()) {
                HttpResponse response = httpClient.execute(new HttpGet(generateLoginURL(driver)));
                String result = MxAndroidUtil.readResponse(response.getEntity().getContent());
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
                    } catch (NumberFormatException e) {
                        MCLoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
                        if (withKeepAlive) {
                            keepAliveTask.tryToSend();
                        }
                    } catch (SQLException e) {
                        MCLoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
                        if (withKeepAlive) {
                            keepAliveTask.tryToSend();
                        }
                    }
                }
                sendLocations(httpClient, driver);
            }
        } catch (IOException e) {
            MCLoggerFactory.getLogger(getClass()).error(e.getStackTrace());
        } catch (SQLException e) {
            MCLoggerFactory.getLogger(getClass()).error(e.getStackTrace());
        } catch (Exception e) {
            MCLoggerFactory.getLogger(getClass()).error(e.getStackTrace());
        }
    }

    public void sendLocations(final HttpClient client, String driver) throws IOException, SQLException {
        final List<LocationEntity> geoLocations = DistributionDAO.getInstance(DistributionApplication.getContext()).getGeoLocations(driver);
        int locationsCount = geoLocations.size();
        if (locationsCount == 0) {
            return;
        }
        final HttpPost post = new HttpPost(generateLocationURL());
        final Gson gson = new Gson();
        StringBuilder sb = new StringBuilder("[");
        long max = 0;
        for (LocationEntity location : geoLocations) {
            sb.append(gson.toJson(location.toRecord())).append(",");
            max = max > location.getId() ? max : location.getId();
        }
        sb.append("]");
        post.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        post.setHeader(new BasicHeader(HTTP.CHARSET_PARAM, "UTF-8"));
        post.setEntity(new StringEntity(sb.toString().replace(",]", "]"), "UTF-8"));
        MCLoggerFactory.getLogger(getClass()).info("sending " + locationsCount + " locations...");
        final HttpResponse response = client.execute(post);
        HttpEntity entity = response.getEntity();
        if (entity == null) {
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