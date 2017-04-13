package com.magenta.mc.client.android.service;

import android.app.IntentService;
import android.content.Intent;

import com.magenta.mc.client.android.common.Constants;
import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.db.dao.DistributionDAO;
import com.magenta.mc.client.android.http.HttpClient;
import com.magenta.mc.client.android.log.MCLoggerFactory;

import java.sql.SQLException;

public class SenderService extends IntentService {

    public SenderService() {
        super(Constants.SENDER_SERVICE_NAME);
    }

    protected void onHandleIntent(Intent intent) {
        // TODO: 2/16/17 сделать отправку через сервис
        sendPoints();
    }

    private void sendPoints() {
        try {
            HttpClient.getInstance().sendLocations(DistributionDAO.getInstance().getGeoLocations(Settings.get().getUserId()));
        } catch (SQLException e) {
            MCLoggerFactory.getLogger(SenderService.class).error(e.getMessage(), e);
        }
    }
}