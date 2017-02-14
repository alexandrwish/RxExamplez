package com.magenta.mc.client.android.mc.util;

import com.magenta.mc.client.android.mc.tracking.GeoLocation;

/**
 * Created by IntelliJ IDEA.
 * User: const
 * Date: 14.12.11
 * Time: 11:44
 * To change this template use File | Settings | File Templates.
 */
public class PlatfromUtilDefault implements PlatformUtil {
    public boolean startConnection() {
        return true;
    }

    public boolean closeConnection() {
        return true;
    }

    public String getImei() {
        throw new UnsupportedOperationException("getImei is not supported in base class, please override it");
    }

    public GeoLocation getGeoLocation(int locationMaxAge) {
        throw new UnsupportedOperationException("getGeoLocation is not supported in base class, please override it");
    }

    public void initLocationAPI() {
        throw new UnsupportedOperationException("initLocationAPI is not supported in base class, please override it");
    }

    public void shutdownLocationAPI() {
        throw new UnsupportedOperationException("shutdownLocationAPI is not supported in base class, please override it");
    }
}
