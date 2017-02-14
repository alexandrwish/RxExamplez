package com.magenta.mc.client.android.mc.util;

import com.magenta.mc.client.android.mc.tracking.GeoLocation;

/**
 * Created by IntelliJ IDEA.
 * User: const
 * Date: 14.12.11
 * Time: 11:34
 * To change this template use File | Settings | File Templates.
 */
public interface PlatformUtil {

    /**
     * Establishes an internet connection using native WindowsMobile(WM) API.
     * In non-WM environment does nothing.
     *
     * @return true if opened successfully, false otherwise
     */
    boolean startConnection();

    boolean closeConnection();

    /**
     * Speed should be in kilometers per hour
     *
     * @param locationMaxAge
     * @return
     */
    GeoLocation getGeoLocation(int locationMaxAge);

    String getImei();

    void initLocationAPI();

    void shutdownLocationAPI();
}
