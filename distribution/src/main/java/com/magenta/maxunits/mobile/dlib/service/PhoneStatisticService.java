package com.magenta.maxunits.mobile.dlib.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.google.gson.Gson;
import com.magenta.maxunits.mobile.dlib.DistributionApplication;
import com.magenta.maxunits.mobile.dlib.db.dao.DistributionDAO;
import com.magenta.maxunits.mobile.dlib.entity.PhoneStatisticEntity;
import com.magenta.maxunits.mobile.dlib.record.PhoneStatisticRecord;
import com.magenta.maxunits.mobile.dlib.rpc.DistributionRPCOut;
import com.magenta.mc.client.log.MCLoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PhoneStatisticService extends Service {

    int MINUTE = 60 * 1000;
    int FIFTEEN_MINUTES = 15 * MINUTE;
    CountDownTimer cdt = null;
    DistributionDAO dao = null;
    boolean killMePlz = false;

    public void onCreate() {
        super.onCreate();
        dao = DistributionDAO.getInstance(getApplicationContext());
        cdt = new CountDownTimer(FIFTEEN_MINUTES, MINUTE) {
            public void onTick(long millisUntilFinished) {
                try {
                    PhoneStatisticEntity entity = new PhoneStatisticEntity();
                    entity.setBatteryState(getBatteryLevel());
                    entity.setGpsState(isGPSEnable());
                    entity.setGprsState(isNetworkAvailable());
                    entity.setDate(new Date());
                    dao.createPhoneState(entity);
                    if (entity.getBatteryState() > 10) {
                        ((DistributionApplication) DistributionApplication.getInstance()).lock();
                    } else {
                        ((DistributionApplication) DistributionApplication.getInstance()).unlock();
                    }
                    MCLoggerFactory.getLogger(getClass()).info("PhoneState. bat:" + entity.getBatteryState() + ", gps:" + entity.isGpsState() + ", gprs:" + entity.isGprsState());
                } catch (SQLException ignore) {
                }
            }

            public void onFinish() {
                if (!killMePlz) {
                    try {
                        List<PhoneStatisticRecord> records = new ArrayList<PhoneStatisticRecord>();
                        Date maxDate = null;
                        for (PhoneStatisticEntity e : dao.getPhoneStatistics()) {
                            records.add(e.toRecord());
                            if (maxDate == null || maxDate.before(e.getDate())) {
                                maxDate = e.getDate();
                            }
                        }
                        if (maxDate != null) {
                            DistributionRPCOut.savePhoneState(new Gson().toJson(records), maxDate);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    cdt.start();
                }
            }
        };
        cdt.start();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        killMePlz = true;
        cdt.cancel();
        super.onDestroy();
    }

    private float getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        // Error checking that probably isn't needed but I added just in case.
        if (level == -1 || scale == -1) {
            return -1f;
        }
        return ((float) level / (float) scale) * 100.0f;
    }

    private boolean isGPSEnable() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}