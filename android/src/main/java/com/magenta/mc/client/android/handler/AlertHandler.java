package com.magenta.mc.client.android.handler;

import android.os.Handler;
import android.os.Looper;

import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.entity.AbstractStop;
import com.magenta.mc.client.android.events.AlertEvent;
import com.magenta.mc.client.android.service.ServicesRegistry;
import com.magenta.mc.client.android.service.storage.entity.Job;
import com.magenta.mc.client.android.service.storage.entity.Stop;

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
        if (Settings.get().getAudioAlert() && !started) {
            started = true;
            killMePlz = false;
            delay = Settings.get().getAlertDelay() * 60 * 1000;
            postDelayed(new AlertRunnable(), delay);
        }
    }

    public void stop() {
        killMePlz = true;
    }

    private class AlertRunnable implements Runnable {

        public void run() {
            List<String> runs = new ArrayList<>();
            List<String> jobs = new ArrayList<>();
            boolean needToIncrement;
            for (Job job : (List<Job>) ServicesRegistry.getDataController().loadCurrentJobs()) {
                needToIncrement = false;
                for (AbstractStop stop : job.getStops()) {
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