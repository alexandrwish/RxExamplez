package com.magenta.mc.client.android.util;

import android.content.Context;
import android.content.res.Resources;

import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.util.ResourceManager;

import java.io.InputStream;

public class AndroidResourceManager extends ResourceManager {

    private Context applicationContext;

    public AndroidResourceManager(Context context) {
        this.applicationContext = context;
    }

    public InputStream getResourceAsStream(String name) {
        try {
            Resources resources = applicationContext.getResources();
            String nameForId = name.replace(".properties", ""); //todo support other extensions if necessary
            nameForId = nameForId.replace(".zip", "");
            return resources.openRawResource(getIdForResource(nameForId, "raw"));
        } catch (Resources.NotFoundException e) {
            MCLoggerFactory.getLogger(getClass()).debug(String.format("Error while loading resource with name: %s, trying getClass().getClassLoader().getResourceAsStream(name)", name));
            return super.getResourceAsStream(name);
        }
    }

    public int getIdForResource(String nameForId, String type) {
        Resources resources = applicationContext.getResources();
        String packageName = applicationContext.getPackageName();
        return resources.getIdentifier(nameForId, type, packageName);
    }
}