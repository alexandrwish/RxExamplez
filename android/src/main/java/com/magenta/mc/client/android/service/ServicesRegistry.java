package com.magenta.mc.client.android.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.magenta.mc.client.android.McAndroidApplication;
import com.magenta.mc.client.android.log.MCLoggerFactory;

public class ServicesRegistry {

    private static Class<? extends CoreService> coreServiceClass;
    private static DataController dataController;
    private static CoreService coreService;
    private static final ServiceConnection CORE_SERVICE_CONNECTION = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            MCLoggerFactory.getLogger(ServicesRegistry.class).debug("Core service connected");
            coreService = ((CoreServiceGeneric.LocalBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            MCLoggerFactory.getLogger(ServicesRegistry.class).debug("Core service disconnected");
            coreService = null;
        }
    };

    public static void registerDataController(final DataController dataController) {
        ServicesRegistry.dataController = dataController;
    }

    public static DataController getDataController() {
        return dataController;
    }

    @SuppressWarnings("unchecked")
    public static <T extends CoreService> T getCoreService() {
        if (coreService != null) {
            return (T) coreService;
        }
        //todo check why service is disconnected
        connectToCoreService();
        return (T) coreService;
    }

    private static void connectToCoreService() {
        MCLoggerFactory.getLogger(ServicesRegistry.class).warn("binding service");
        if (!McAndroidApplication.getInstance().bindService(new Intent(McAndroidApplication.getInstance(), coreServiceClass), CORE_SERVICE_CONNECTION, Context.BIND_AUTO_CREATE)) {
            throw new RuntimeException("Service not bound");
        }
    }

    public static void startCoreService(Class<? extends CoreService> coreServiceClass) {
        ServicesRegistry.coreServiceClass = coreServiceClass;
        connectToCoreService();
    }

    public static void stopCoreService() {
        if (coreService != null) {
            coreService.stopSelf();
        }
    }
}