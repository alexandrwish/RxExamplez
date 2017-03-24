package com.magenta.mc.client.android.service;

import android.app.IntentService;
import android.content.Intent;

import com.magenta.mc.client.android.common.Constants;
import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.db.dao.DistributionDAO;
import com.magenta.mc.client.android.entity.LocationEntity;
import com.magenta.mc.client.android.http.HttpClient;
import com.magenta.mc.client.android.log.MCLoggerFactory;
import com.magenta.mc.client.android.tracking.GeoLocation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
            List<LocationEntity> entities = DistributionDAO.getInstance().getGeoLocations(Settings.get().getUserId());
            List<GeoLocation> locations = new ArrayList<>(entities.size());
            for (LocationEntity entity : entities) {
                locations.add(new GeoLocation(entity.getDate(), entity.getLat(), entity.getLon(), entity.getSpeed(), 0F, 0));
            }
            HttpClient.getInstance().sendLocations(System.currentTimeMillis(), locations);
        } catch (SQLException e) {
            MCLoggerFactory.getLogger(SenderService.class).error(e.getMessage(), e);
        }
    }
}