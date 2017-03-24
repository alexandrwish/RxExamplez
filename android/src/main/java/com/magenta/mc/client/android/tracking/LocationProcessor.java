package com.magenta.mc.client.android.tracking;

import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.log.MCLoggerFactory;
import com.magenta.mc.client.android.resender.Resender;

import java.util.ArrayList;
import java.util.List;

public class LocationProcessor {

    private GeoLocationServiceConfig config;

    private List<GeoLocation> coordinates;
    private int addedLocationsInStoppedState;
    private boolean isStartDetecting;    // is now "detecting" start event
    private long lastDetectingTimestamp; // when was recieved last location
    private long detectingTime;
    private GeoLocationState currentState = GeoLocationState.UNKNOWN;
    private List<GeoLocation> detectLocations = new ArrayList<>(100);

    public LocationProcessor(GeoLocationServiceConfig config) {
        this.config = config;
        coordinates = new ArrayList<>(config.getBatchCover() / config.getRetrieveInterval() + 1);
    }

    public void processLocationAndSendBatchIfNecessary(GeoLocation location) {
        location.setUserId(Settings.get().getUserId());
        MCLoggerFactory.getLogger(getClass()).debug("process location: " + location);
        if (location.getState() == null) {
            location.setState(currentState);
        }

        if (location.getState() == GeoLocationState.MOTIONLESS || location.getState() == GeoLocationState.UNKNOWN) {
            if (config.getMotionlessSendIntervalMultiplexer() < 0) { //if negative then don't process lcoation
                return;
            }
            addedLocationsInStoppedState %= config.getMotionlessSendIntervalMultiplexer();
            if (addedLocationsInStoppedState != 0) {
                MCLoggerFactory.getLogger(getClass()).debug("don't process location, because in " + location.getState());
                addedLocationsInStoppedState++;
                return;
            }
            addedLocationsInStoppedState++;
        } else {
            addedLocationsInStoppedState = 0;
        }
        coordinates.add(location);
        /* check to prevent buffer overflow */
        if (coordinates.size() > 100) {
            coordinates.remove(0);
        }
        long firstDate = coordinates.get(0).getRetrieveTimestamp();
        long currDate = location.getRetrieveTimestamp();
        if (currDate - firstDate >= config.getBatchCover()
                || firstDate > currDate  //if there some time singularity fix all
                || config.getBatchCover() == config.getRetrieveInterval()) {
            List<GeoLocation> coordinatesToPersist = coordinates;
            coordinates = new ArrayList<>(config.getBatchCover() / config.getRetrieveInterval() + 1);
            GeoLocationBatch batch = new GeoLocationBatch(coordinatesToPersist);
            Resender.getInstance().send(batch);
        }
    }

    public boolean checkStartStop(GeoLocation location) {
        if (location != null) {
            float speed = location.getSpeed();
            if (config.getMovingSpeed()[0] < speed && speed < config.getMovingSpeed()[1]) { //moving
                if (!isStartDetecting) {
                    resetDetectingVariables(location);
                    isStartDetecting = true;
                } else {
                    return detectAndProcessIfNecessary(location,
                            config.getStartDetectingTime(),
                            config.getStartDetectingMinLocationsQuantity(),
                            GeoLocationState.IN_MOTION,
                            GeoLocationState.STARTING
                    );
                }
            } else if (speed < config.getMovingSpeed()[0]) { //motionless
                if (isStartDetecting) {
                    resetDetectingVariables(location);
                    isStartDetecting = false;
                } else {
                    return detectAndProcessIfNecessary(location,
                            config.getStopDetectingTime(),
                            config.getStopDetectingMinLocationsQuantity(),
                            GeoLocationState.MOTIONLESS,
                            GeoLocationState.STOPPING
                    );
                }
            }

        }
        return false;
    }

    private void resetDetectingVariables(GeoLocation location) {
        detectingTime = 0;
        lastDetectingTimestamp = System.currentTimeMillis();
        detectLocations.clear();
        detectLocations.add(location);
    }

    private boolean detectAndProcessIfNecessary(GeoLocation location,
                                                int configDetectingTime,
                                                int configLocationsQuant,
                                                GeoLocationState longState,
                                                GeoLocationState changingState) {
        detectLocations.add(location);
        if (lastDetectingTimestamp != 0) {
            detectingTime += System.currentTimeMillis() - lastDetectingTimestamp;
        }
        lastDetectingTimestamp = System.currentTimeMillis();
        if (detectingTime >= configDetectingTime
                && detectLocations.size() >= configLocationsQuant
                && currentState != longState) {     //i.e. already IN_MOTION, don't send STARTING event
            location.setState(changingState);
            currentState = longState;
            processLocationAndSendBatchIfNecessary(location);
            String message = "change location state was detected: " + changingState +
                    "\n speed: " + location.getSpeed() +
                    "\n detectingTime: " + detectingTime +
                    "\n lastDetectingTimestamp: " + lastDetectingTimestamp +
                    "\n detectLocationsQuant: " + detectLocations.size() + ":";
            int startIndex = detectLocations.size() - 20 > 0 ? detectLocations.size() - 20 : 0;
            for (int i = startIndex; i < detectLocations.size(); i++) {
                message += "\n    " + detectLocations.get(i);
            }
            MCLoggerFactory.getLogger(getClass()).debug(message);
            return true;
        }
        return false;
    }
}