package com.magenta.mc.client.android.util;

import java.io.InputStream;
import java.net.URL;

/**
 * Author: Petr Popov
 * Created: 31.01.2011 16:44:46
 * <p/>
 * Copyright (c) 1999-2010 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 * <p/>
 */
public class ResourceManager {

    protected static ResourceManager instance;

    protected ResourceManager() {
    }

    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }

    public static void init(ResourceManager manager) {
        instance = manager;
    }

    /**
     * Load resource from app jar, or from mc-client jar if app jar does not have resource
     *
     * @param name
     * @return
     */
    public InputStream getResourceAsStream(String name) {
        return getClass().getClassLoader().getResourceAsStream(name);
    }

    /**
     * Load resource from app jar, or from mc-client jar if app jar does not have resource
     *
     * @param name
     * @return
     */
    public URL getResource(String name) {
        return getClass().getClassLoader().getResource(name);
    }

}
