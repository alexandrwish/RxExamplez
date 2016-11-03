package com.magenta.maxunits.mobile.service;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.magenta.maxunits.mobile.MxApplication;
import com.magenta.mc.client.log.MCLoggerFactory;

import net.sf.microlog.core.LoggerFactory;

import java.lang.reflect.Constructor;

public class ServicesRegistry {

    static Application application;
    static Class<? extends CoreService> coreServiceClass;
    static Class<? extends WorkflowService> workflowServiceClass;
    static Class<? extends SaveLocationsService> saveLocationsServiceClass;
    static DataController dataController;
    static CoreService coreService;
    static final ServiceConnection CORE_SERVICE_CONNECTION = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            MCLoggerFactory.getLogger(ServicesRegistry.class).debug("Core service connected");
            coreService = ((CoreServiceGeneric.LocalBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            MCLoggerFactory.getLogger(ServicesRegistry.class).debug("Core service disconnected");
            coreService = null;
        }
    };
    static SaveLocationsService saveLocationsService;
    static final ServiceConnection SAVE_LOCATION_SERVICE = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
            MCLoggerFactory.getLogger(ServicesRegistry.class).debug("Save location service connected");
            saveLocationsService = ((SaveLocationsService.LocalBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName name) {

        }
    };
    static WorkflowService workflowService;
    static LocationService sLocationService;
    protected static ServiceConnection sLocationServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            LoggerFactory.getLogger(ServicesRegistry.class).trace("LocationService service connected");
            sLocationService = ((LocationService.LocalBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            LoggerFactory.getLogger(ServicesRegistry.class).trace("LocationService service disconnected");
            sLocationService = null;
        }
    };

    public static void registerDataController(final DataController dataController) {
        ServicesRegistry.dataController = dataController;
    }

    public static void startSaveLocationsService(final Application application, Class<? extends SaveLocationsService> saveLocationsServiceClass) {
        ServicesRegistry.application = application;
        ServicesRegistry.saveLocationsServiceClass = saveLocationsServiceClass;
        connectToSaveLocationsServiceClass();
    }

    private static void connectToSaveLocationsServiceClass() {
        if (!application.bindService(new Intent(application, saveLocationsServiceClass), SAVE_LOCATION_SERVICE, Context.BIND_AUTO_CREATE)) {
            throw new RuntimeException("Service not bound");
        }
    }

    public static void stopSaveLocationsService() {
        if (saveLocationsService != null) {
            saveLocationsService.kill();
            saveLocationsService.stopSelf();
        }
    }

    public static void registerWorkflowService(final Class<? extends WorkflowService> workflowServiceClass) {
        ServicesRegistry.workflowServiceClass = workflowServiceClass;
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

    static void connectToCoreService() {
        MCLoggerFactory.getLogger(ServicesRegistry.class).warn("binding service");
        if (!application.bindService(new Intent(application, coreServiceClass), CORE_SERVICE_CONNECTION, Context.BIND_AUTO_CREATE)) {
            throw new RuntimeException("Service not bound");
        }
    }

    public static void startCoreService(final Application application, Class<? extends CoreService> coreServiceClass) {
        ServicesRegistry.application = application;
        ServicesRegistry.coreServiceClass = coreServiceClass;
        connectToCoreService();
    }

    public static void stopCoreService() {
        if (coreService != null) {
            coreService.stopSelf();
        }
    }

    public static WorkflowService newWorkflowService(final Context context) {
        if (workflowServiceClass == null) {
            throw new IllegalStateException("WorkflowService not registered");
        }
        try {
            final Constructor constructor = workflowServiceClass.getConstructor(Context.class);
            workflowService = (WorkflowService) constructor.newInstance(context);
            return workflowService;
        } catch (Exception e) {
            throw new RuntimeException("Can't create new instance of WorkflowService", e);
        }
    }

    public static WorkflowService getWorkflowService() {
        if (workflowService == null) {
            return newWorkflowService(MxApplication.getContext());
        }
        return workflowService;
    }

    public static void startLocationService(Context application, Class<? extends LocationService> locationServiceClass) {
        if (!application.bindService(new Intent(application, locationServiceClass), sLocationServiceConnection, Context.BIND_AUTO_CREATE)) {
            throw new RuntimeException("Service not bound");
        }
    }

    public static <T extends LocationService> T getLocationService() {
        if (sLocationService == null) {
            return null;
        }
        return (T) sLocationService;
    }
}