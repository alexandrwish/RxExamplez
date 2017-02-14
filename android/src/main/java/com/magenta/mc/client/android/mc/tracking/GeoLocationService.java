package com.magenta.mc.client.android.mc.tracking;

import com.magenta.mc.client.android.MobileApp;
import com.magenta.mc.client.android.mc.components.MCTimerTask;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.setup.Setup;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class GeoLocationService {

    private static GeoLocationService instance;
    private final List listeners = new ArrayList();
    private GeoLocationServiceConfig config;
    private MCTimerTask retrieveCoordinateTask;
    private boolean started;

    private GeoLocationService() {
    }

    public static GeoLocationService getInstance() {
        if (instance == null) {
            instance = new GeoLocationService();
        }
        return instance;
    }

    public void init(GeoLocationServiceConfig config) {
        this.config = config;
    }

    public void addListener(LocationUpdatedListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeListener(LocationUpdatedListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public void notifyLocationUpdateListeners() {
        for (int i = 0; i < listeners.size(); i++) {
            ((LocationUpdatedListener) listeners.get(i)).handleLocationUpdate();
        }
    }

    public void start(boolean invokedFromLoginListener) {
        MCLoggerFactory.getLogger(getClass()).debug("start from login: " + invokedFromLoginListener + "  started: " + started);
        if (config.isEnabled() && !started && (!invokedFromLoginListener || !config.isLogoutEnabled())) {
            Setup.get().getPlatformUtil().initLocationAPI();

            final Timer timer = MobileApp.getInstance().getTimer();

            retrieveCoordinateTask = new MCTimerTask() {

                private int timerTaskInvoked;

                private int timerTaskInvocationsMaxQuantity;

                private LocationProcessor processor = new LocationProcessor(config);

                {
                    if (config.isStartStopEnabled()) {
                        timerTaskInvocationsMaxQuantity = config.getRetrieveInterval() / config.getStartStopInterval();
                    } else {
                        timerTaskInvocationsMaxQuantity = 1;
                    }
                }

                public void runTask() {
                    GeoLocation location = null;
                    if (!config.isFakeMode()) {
                        location = Setup.get().getPlatformUtil().getGeoLocation(config.getLocationMaxAge());
                    } else {
                        location = new GeoLocation(new Long(System.currentTimeMillis()),
                                new Double(53.392739),
                                new Double(50.169675),
                                new Float(4),
                                new Float(4),
                                new Integer(4));
                        location.setRetrieveTimestamp(new Long(System.currentTimeMillis()));
                        location.setSource(GeoLocationSource.GPS);
                    }
                    boolean isLocationWasProcessedInCheckStartStopMethod = false;
                    if (config.isStartStopEnabled()) {
                        isLocationWasProcessedInCheckStartStopMethod = processor.checkStartStop(location);
                    }
                    if (timerTaskInvoked == 0
                            && !isLocationWasProcessedInCheckStartStopMethod) {
                        if (location != null) {
                            processor.processLocationAndSendBatchIfNecessary(location);
                        } else {
                            MCLoggerFactory.getLogger(getClass()).debug("location is null");
                        }
                    }
                    timerTaskInvoked++;
                    timerTaskInvoked %= timerTaskInvocationsMaxQuantity;
                }
            };

            int retrieveInterval = config.isStartStopEnabled() ? config.getStartStopInterval() : config.getRetrieveInterval();

            timer.schedule(
                    retrieveCoordinateTask,
                    retrieveInterval,
                    retrieveInterval
            );

            started = true;
        }
    }

    public void stop(boolean invokedFromLoginListener) {
        if (config.isEnabled() && started && (!invokedFromLoginListener || !config.isLogoutEnabled())) {
            if (retrieveCoordinateTask != null) {
                retrieveCoordinateTask.cancel();
                retrieveCoordinateTask = null;
            }
            Setup.get().getPlatformUtil().shutdownLocationAPI();
            started = false;
        }
    }

}