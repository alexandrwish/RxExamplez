package com.magenta.mc.client.tracking;

import com.magenta.mc.client.util.StrUtil;

import java.util.Properties;

/**
 * @author Petr Popov
 *         Created: 06.12.11 20:44
 */
public class GeoLocationServiceConfig {

    private boolean enabled;

    private boolean logoutEnabled;

    private boolean handlerMode;

    private int retrieveInterval;

    private int batchCover;

    private int locationMaxAge;

    private boolean startStopEnabled;

    private int startStopInterval;

    private int motionlessSendIntervalMultiplexer;
    private int stopDetectingTime;
    private int stopDetectingMinLocationsQuantity;
    private int startDetectingTime;
    private int startDetectingMinLocationsQuantity;
    private float[] movingSpeed;
    private boolean fakeMode;

    public GeoLocationServiceConfig(Properties props) {
        enabled = "true".equals(props.getProperty("tracking.enabled", "false").trim());
        logoutEnabled = "true".equals(props.getProperty("tracking.enabled.logout", "false").trim());
        retrieveInterval = Integer.parseInt(props.getProperty("tracking.retrieveIntervalSec", "30")) * 1000;
        batchCover = Integer.parseInt(props.getProperty("tracking.batchCoverSec", "120")) * 1000;
        locationMaxAge = Integer.parseInt(props.getProperty("tracking.locationMaxAge", "600")) * 1000;
        fakeMode = "true".equals(props.getProperty("tracking.fake", "false").trim());

        startStopEnabled = "true".equals(props.getProperty("tracking.startStop.enabled", "false").trim());

        motionlessSendIntervalMultiplexer = Integer.parseInt(props.getProperty("tracking.startStop.motionlessSendIntervalMultiplexer", "1"));
        if (startStopEnabled) {
            startStopInterval = Integer.parseInt(props.getProperty("tracking.startStop.pollIntervalSec", "5")) * 1000;
            movingSpeed = parseSpeedInterval(props.getProperty("tracking.startStop.movingSpeedKmHour", "5-200"));
            stopDetectingTime = Integer.parseInt(props.getProperty("tracking.startStop.stopDetectingTimeSec", "60")) * 1000;
            stopDetectingMinLocationsQuantity = Integer.parseInt(props.getProperty("tracking.startStop.stopDetectingMinLocationsQuantity", "3"));
            startDetectingTime = Integer.parseInt(props.getProperty("tracking.startStop.startDetectingTimeSec", "60")) * 1000;
            startDetectingMinLocationsQuantity = Integer.parseInt(props.getProperty("tracking.startStop.startDetectingMinLocationsQuantity", "3"));
        }

    }

    private float[] parseSpeedInterval(String interval) {
        float[] result = new float[2];
        String[] minMaxSpeed = StrUtil.split(interval, "-");
        result[0] = Float.parseFloat(minMaxSpeed[0]);
        result[1] = Float.parseFloat(minMaxSpeed[1]);
        return result;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isHandlerMode() {
        return handlerMode;
    }

    public boolean isLogoutEnabled() {
        return logoutEnabled;
    }

    public int getRetrieveInterval() {
        return retrieveInterval;
    }

    public int getBatchCover() {
        return batchCover;
    }

    public int getLocationMaxAge() {
        return locationMaxAge;
    }

    public boolean isStartStopEnabled() {
        return startStopEnabled;
    }

    public int getStartStopInterval() {
        return startStopInterval;
    }

    public int getStopDetectingTime() {
        return stopDetectingTime;
    }

    public int getStartDetectingTime() {
        return startDetectingTime;
    }

    public float[] getMovingSpeed() {
        return movingSpeed;
    }

    public int getStopDetectingMinLocationsQuantity() {
        return stopDetectingMinLocationsQuantity;
    }

    public int getStartDetectingMinLocationsQuantity() {
        return startDetectingMinLocationsQuantity;
    }

    public int getMotionlessSendIntervalMultiplexer() {
        return motionlessSendIntervalMultiplexer;
    }

    public boolean isFakeMode() {
        return fakeMode;
    }
}
