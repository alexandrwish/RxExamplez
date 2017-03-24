package com.magenta.mc.client.android.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.magenta.mc.client.android.McAndroidApplication;
import com.magenta.mc.client.android.log.MCLoggerFactory;

import net.sf.microlog.core.LoggerFactory;

public class ServicesRegistry {

    private static SaveLocationsService saveLocationsService;
    private static final ServiceConnection SAVE_LOCATION_SERVICE = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
            MCLoggerFactory.getLogger(ServicesRegistry.class).debug("Save location service connected");
            saveLocationsService = ((SaveLocationsService.LocalBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private static LocationService sLocationService;
    private static final ServiceConnection sLocationServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            LoggerFactory.getLogger(ServicesRegistry.class).trace("LocationService service connected");
            sLocationService = ((LocationService.LocalBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            LoggerFactory.getLogger(ServicesRegistry.class).trace("LocationService service disconnected");
            sLocationService = null;
        }
    };

    private static Class<? extends CoreService> coreServiceClass;
    private static Class<? extends SaveLocationsService> saveLocationsServiceClass;
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

    public static void startSaveLocationsService(Class<? extends SaveLocationsService> saveLocationsServiceClass) {
        ServicesRegistry.saveLocationsServiceClass = saveLocationsServiceClass;
        connectToSaveLocationsServiceClass();
    }

    private static void connectToSaveLocationsServiceClass() {
        if (!McAndroidApplication.getInstance().bindService(new Intent(McAndroidApplication.getInstance(), saveLocationsServiceClass), SAVE_LOCATION_SERVICE, Context.BIND_AUTO_CREATE)) {
            throw new RuntimeException("Service not bound");
        }
    }

    public static void stopSaveLocationsService() {
        if (saveLocationsService != null) {
            saveLocationsService.kill();
            saveLocationsService.stopSelf();
        }
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

    public static void startLocationService(Context application, Class<? extends LocationService> locationServiceClass) {
        if (!application.bindService(new Intent(application, locationServiceClass), sLocationServiceConnection, Context.BIND_AUTO_CREATE)) {
            throw new RuntimeException("Service not bound");
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends LocationService> T getLocationService() {
        if (sLocationService == null) {
            return null;
        }
        return (T) sLocationService;
    }
}