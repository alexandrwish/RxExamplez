package com.magenta.mc.client.android.service;

import android.app.IntentService;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.magenta.hdmate.mx.model.CancelationReason;
import com.magenta.hdmate.mx.model.JobRecord;
import com.magenta.hdmate.mx.model.SettingsResultRecord;
import com.magenta.mc.client.android.common.Constants;
import com.magenta.mc.client.android.common.IntentAttributes;
import com.magenta.mc.client.android.db.dao.DistributionDAO;
import com.magenta.mc.client.android.entity.MapProviderType;
import com.magenta.mc.client.android.entity.MapSettingsEntity;
import com.magenta.mc.client.android.http.HttpClient;
import com.magenta.mc.client.android.mc.MxSettings;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.record.LoginResultRecord;
import com.magenta.mc.client.android.ui.activity.common.LoginActivity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class HttpService extends IntentService {

    public HttpService() {
        super(Constants.HTTP_SERVICE_NAME);
    }

    private static boolean updateSettings(MapSettingsEntity entity, Map<String, Map<String, String>> mapSettings) throws SQLException {
        if (mapSettings.size() == 1) {
            entity.setDriver(Setup.get().getSettings().getLogin());
            for (Map.Entry<String, Map<String, String>> entry : mapSettings.entrySet()) {
                entity.setProvider(entry.getKey());
                entity.setMapProviderType(MapProviderType.GOOGLE.name().equalsIgnoreCase(entry.getKey())
                        ? MapProviderType.GOOGLE
                        : (MapProviderType.YANDEX.name().equalsIgnoreCase(entry.getKey())
                        ? MapProviderType.YANDEX
                        : MapProviderType.LEAFLET));
            }
            entity.setSettings(new Gson().toJson(mapSettings));
            DistributionDAO.getInstance().saveMapSettings(entity);
            return true;
        }
        return false;
    }

    protected void onHandleIntent(Intent intent) {
        if (intent.hasExtra(IntentAttributes.HTTP_TYPE)) {
            switch (intent.getIntExtra(IntentAttributes.HTTP_TYPE, 0)) {
                case (Constants.LOGIN_TYPE): {
                    HttpClient.getInstance().init();
                    login(intent);
                    break;
                }
                case (Constants.SETTINGS_TYPE): {
                    getSettings();
                    break;
                }
                case (Constants.JOBS_TYPE): {
                    getJobs();
                    break;
                }
            }
        }
    }

    private void login(Intent intent) {
        HttpClient.getInstance().login(intent.getStringExtra(IntentAttributes.HTTP_ACCOUNT),
                intent.getStringExtra(IntentAttributes.HTTP_LOGIN),
                intent.getStringExtra(IntentAttributes.HTTP_PASS))
                .enqueue(new Callback<LoginResultRecord>() {
                    public void onResponse(Call<LoginResultRecord> call, Response<LoginResultRecord> response) {
                        if (response != null && response.body() != null) {
                            LoginResultRecord result = response.body();
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(Constants.AUTH_TOKEN, result.getToken()).apply();
                            sendBroadcast(new Intent(Constants.HTTP_SERVICE_NAME).putExtra(IntentAttributes.HTTP_LOGIN_RESPONSE_TYPE, Constants.OK)); //все хорошо
                        } else {
                            sendBroadcast(new Intent(Constants.HTTP_SERVICE_NAME).putExtra(IntentAttributes.HTTP_LOGIN_RESPONSE_TYPE, Constants.WARN)); //что-то пошло не так
                        }
                    }

                    public void onFailure(Call<LoginResultRecord> call, Throwable t) {
                        MCLoggerFactory.getLogger(HttpService.class).error(t.getMessage(), t);
                        sendBroadcast(new Intent(Constants.HTTP_SERVICE_NAME).putExtra(IntentAttributes.HTTP_LOGIN_RESPONSE_TYPE, Constants.ERROR)); //все плохо
                    }
                });
    }

    private void getSettings() {
        HttpClient.getInstance().getSettings()
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<SettingsResultRecord>() {
                    public void onCompleted() {
                        sendBroadcast(new Intent(Constants.HTTP_SERVICE_NAME).putExtra(IntentAttributes.HTTP_SETTINGS_RESPONSE_TYPE, Constants.OK));
                    }

                    public void onError(Throwable e) {
                        MCLoggerFactory.getLogger(LoginActivity.class).error(e.getMessage(), e);
                        sendBroadcast(new Intent(Constants.HTTP_SERVICE_NAME).putExtra(IntentAttributes.HTTP_SETTINGS_RESPONSE_TYPE, Constants.ERROR));
                    }

                    public void onNext(SettingsResultRecord result) {
                        if (result == null) return;
                        com.magenta.mc.client.android.common.Settings.SettingsBuilder.get().start()
                                .setFactCost(result.getAllowFactCost())
                                .setBarcodeScreen(result.getEnableBarcodeScreen())
                                .setSignatureScreen(result.getEnableSignatureScreen())
                                .setRandomOrders(result.getAllowToPassInArbitraryOrder())
                                .setSeveralRuns(result.getAllowToPassSeveralRuns())
                                .setNonCompleted(result.getNonCompletedTimePeriod())
                                .setDisplayMap(result.getEnableMapDisplaying())
                                .setDispatcherPhone(result.getDispatcherPhone())
                                .setShowAlert(result.getAlertEnable())
                                .setAlertDelay(result.getAlertDelay())
                                .setCapacityUnit(result.getCapacityUnits())
                                .setVolumeUnit(result.getVolumeUnits())
                                .setDefaultMap(result.getDefaultMap())
                                .apply();
                        List<String> reasons = new ArrayList<>(result.getOrderCancelReasons().size());
                        for (CancelationReason reason : result.getOrderCancelReasons()) {
                            reasons.add(reason.getTitle());
                        }
                        MxSettings.getInstance().setOrderCancelReasons(reasons);
                        MxSettings.getInstance().saveSettings();
                        Map<String, Map<String, String>> mapSettings = result.getMapProperties();
                        for (String s : MxSettings.ignoredMapProviders) {
                            mapSettings.remove(s);
                        }
                        if (result.getMapProperties().isEmpty()) {
                            HashMap<String, String> osm = new HashMap<>(1);
                            osm.put("use_map_provider", "true");
                            result.getMapProperties().put("openstreetmap", osm);
                        }
                        try {
                            List<MapSettingsEntity> mapSettingsEntities = DistributionDAO.getInstance().getMapSettings(Setup.get().getSettings().getLogin());
                            if (!mapSettingsEntities.isEmpty()) {
                                if (!mapSettings.containsKey(mapSettingsEntities.get(0).getProvider()) || !mapSettingsEntities.get(0).isRemember()) {
                                    if (updateSettings(mapSettingsEntities.get(0), mapSettings)) {
                                        sendBroadcast(new Intent(Constants.HTTP_SERVICE_NAME).putExtra(IntentAttributes.HTTP_SETTINGS_RESPONSE_TYPE, Constants.WARN));
                                    }
                                }
                            } else {
                                updateSettings(new MapSettingsEntity(), mapSettings);
                            }
                        } catch (SQLException e) {
                            MCLoggerFactory.getLogger(LoginActivity.class).error(e.getMessage(), e);
                        }
                    }
                });
    }

    private void getJobs() {
        HttpClient.getInstance().getJobs()
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<JobRecord>>() {
                    public void onCompleted() {
                        sendBroadcast(new Intent(Constants.HTTP_SERVICE_NAME).putExtra(IntentAttributes.HTTP_JOBS_RESPONSE_TYPE, Constants.STOP));
                    }

                    public void onError(Throwable e) {
                        MCLoggerFactory.getLogger(LoginActivity.class).error(e.getMessage(), e);
                        sendBroadcast(new Intent(Constants.HTTP_SERVICE_NAME).putExtra(IntentAttributes.HTTP_JOBS_RESPONSE_TYPE, Constants.ERROR));
                    }

                    public void onNext(List<JobRecord> jobRecords) {
                        sendBroadcast(new Intent(Constants.HTTP_SERVICE_NAME).putExtra(IntentAttributes.HTTP_JOBS_RESPONSE_TYPE, Constants.START));
                        ServicesRegistry.getDataController().reloadNewJobs(jobRecords);
                    }
                });
    }
}