package com.magenta.maxunits.mobile.dlib.handler;

import android.os.Handler;
import android.os.Looper;

import com.magenta.maxunits.mobile.dlib.service.events.AlertEvent;
import com.magenta.maxunits.mobile.dlib.service.storage.entity.Job;
import com.magenta.maxunits.mobile.dlib.service.storage.entity.Stop;
import com.magenta.maxunits.mobile.mc.MxSettings;
import com.magenta.maxunits.mobile.service.ServicesRegistry;
import com.magenta.mc.client.setup.Setup;

import java.util.ArrayList;
import java.util.List;

public class AlertHandler extends Handler {

    private static AlertHandler instance;
    private long delay;
    private boolean started;
    private boolean killMePlz;

    private AlertHandler() {
        super(Looper.getMainLooper());
        started = false;
        killMePlz = false;
    }

    public static AlertHandler getInstance() {
        if (instance == null) {
            instance = new AlertHandler();
        }
        return instance;
    }

    public void start() {
        if (Setup.get().getSettings().getBooleanProperty(MxSettings.ALERT_ENABLE) && !started) {
            started = true;
            killMePlz = false;
            delay = Setup.get().getSettings().getLongProperty(MxSettings.ALERT_DELAY, 60 * 1000);
            postDelayed(new AlertRunnable(), delay);
        }
    }

    public void stop() {
        killMePlz = true;
    }

    private class AlertRunnable implements Runnable {

        @Override
        public void run() {
            List<String> runs = new ArrayList<String>();
            List<String> jobs = new ArrayList<String>();
            boolean needToIncrement;
            for (Job job : (List<Job>) ServicesRegistry.getDataController().loadCurrentJobs()) {
                needToIncrement = false;
                for (Stop stop : (List<Stop>) job.getStops()) {
                    if (stop.getUpdateType() == Stop.UPDATE_STOP) {
                        needToIncrement = true;
                        jobs.add(stop.getReferenceId());
                    }
                }
                if (needToIncrement) {
                    runs.add(job.getReferenceId());
                }
            }
            if (runs.size() > 0 && jobs.size() > 0 && !killMePlz) {
                ServicesRegistry.getCoreService().notifyListeners(new AlertEvent(runs, jobs));
                postDelayed(this, delay);
            } else {
                started = false;
                killMePlz = false;
            }
        }
    }
}