package com.magenta.mc.client.android.service;

import android.app.IntentService;
import android.content.Intent;

import com.google.gson.Gson;
import com.magenta.hdmate.mx.model.OrderCancelationReason;
import com.magenta.hdmate.mx.model.Run;
import com.magenta.mc.client.android.common.Constants;
import com.magenta.mc.client.android.common.IntentAttributes;
import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.db.dao.DistributionDAO;
import com.magenta.mc.client.android.entity.JobEntity;
import com.magenta.mc.client.android.entity.MapSettingsEntity;
import com.magenta.mc.client.android.entity.type.MapProviderType;
import com.magenta.mc.client.android.http.HttpClient;
import com.magenta.mc.client.android.log.MCLoggerFactory;
import com.magenta.mc.client.android.record.LoginResultRecord;
import com.magenta.mc.client.android.service.renderer.SingleJobRenderer;
import com.magenta.mc.client.android.ui.activity.common.LoginActivity;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.Subscriber;
import rx.schedulers.Schedulers;

public class HttpService extends IntentService {

    public HttpService() {
        super(Constants.HTTP_SERVICE_NAME);
    }

    private static boolean updateSettings(MapSettingsEntity entity, Map<String, Map<String, String>> mapSettings) throws SQLException {
        if (mapSettings.size() == 1) {
            entity.setDriver(Settings.get().getLogin());
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
                case (Constants.ROUTE_TYPE): {
                    getRoute();
                    break;
                }
            }
        }
    }

    private void login(Intent intent) {
        HttpClient.getInstance().login(intent.getStringExtra(IntentAttributes.HTTP_ACCOUNT),
                intent.getStringExtra(IntentAttributes.HTTP_LOGIN),
                intent.getStringExtra(IntentAttributes.HTTP_PASS))
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<LoginResultRecord>() {
                    public void onCompleted() {

                    }

                    public void onError(Throwable e) {
                        MCLoggerFactory.getLogger(HttpService.class).error(e.getMessage(), e);
                        sendBroadcast(new Intent(Constants.HTTP_SERVICE_NAME).putExtra(IntentAttributes.HTTP_LOGIN_RESPONSE_TYPE, Constants.ERROR)); //все плохо
                    }

                    public void onNext(LoginResultRecord result) {
                        if (result.getError()) {
                            sendBroadcast(new Intent(Constants.HTTP_SERVICE_NAME).putExtra(IntentAttributes.HTTP_LOGIN_RESPONSE_TYPE, Constants.WARN)); //все плохо, но не у нас
                        } else {
                            Settings.SettingsBuilder.get().start().setAuthToken(result.getToken()).apply();
                            sendBroadcast(new Intent(Constants.HTTP_SERVICE_NAME).putExtra(IntentAttributes.HTTP_LOGIN_RESPONSE_TYPE, Constants.OK)); //все хорошо
                        }
                    }
                });
    }

    private void getSettings() {
        HttpClient.getInstance().getSettings()
                .subscribeOn(Schedulers.newThread())
                .delay(300, TimeUnit.MILLISECONDS)
                .subscribe(new Subscriber<com.magenta.hdmate.mx.model.Settings>() {
                    public void onCompleted() {
                        sendBroadcast(new Intent(Constants.HTTP_SERVICE_NAME).putExtra(IntentAttributes.HTTP_SETTINGS_RESPONSE_TYPE, Constants.OK));
                    }

                    public void onError(Throwable e) {
                        MCLoggerFactory.getLogger(LoginActivity.class).error(e.getMessage(), e);
                        sendBroadcast(new Intent(Constants.HTTP_SERVICE_NAME).putExtra(IntentAttributes.HTTP_SETTINGS_RESPONSE_TYPE, Constants.ERROR));
                    }

                    public void onNext(com.magenta.hdmate.mx.model.Settings result) {
                        if (result == null) return;
                        Settings.SettingsBuilder.get().start()
                                .setFactCost(result.getAllowFactCost())
                                .setBarcodeScreen(result.getEnableBarcodeScreen())
                                .setSignatureScreen(result.getEnableSignatureScreen())
                                .setRandomOrders(result.getAllowToPassInArbitraryOrder())
                                .setSeveralRuns(result.getAllowToPassSeveralRuns())
                                .setNonCompleted(result.getNonCompletedTimePeriod())
                                .setDisplayMap(result.getEnableMapDisplaying())
                                .setDispatcherPhone(result.getDispatcherPhone())
                                .setAudioAlert(result.getAlertEnable())
                                .setAlertDelay(result.getAlertDelay())
                                .setCapacityUnit(result.getCapacityUnits())
                                .setVolumeUnit(result.getVolumeUnits())
                                .setDefaultMap(result.getDefaultMap())
                                .apply();
                        Set<String> reasons = new HashSet<>();
                        for (OrderCancelationReason reason : result.getOrderCancelationReasons()) {
                            reasons.add(reason.getTitle());
                        }
                        Settings.SettingsBuilder.get().start().setOrderCancellationReasons(reasons);
                        Map<String, Map<String, String>> mapSettings = result.getMapProperties();
                        for (String s : Settings.IGNORED_MAP_PROVIDERS) {
                            mapSettings.remove(s);
                        }
                        if (result.getMapProperties().isEmpty()) {
                            HashMap<String, String> osm = new HashMap<>(1);
                            osm.put("use_map_provider", "true");
                            result.getMapProperties().put("openstreetmap", osm);
                        }
                        Settings.SettingsBuilder.get().start().setMapSettings(mapSettings).apply();
                        try {
                            List<MapSettingsEntity> mapSettingsEntities = DistributionDAO.getInstance().getMapSettings(Settings.get().getLogin());
                            if (!mapSettingsEntities.isEmpty()) {
                                if (!mapSettings.containsKey(mapSettingsEntities.get(0).getProvider()) || !mapSettingsEntities.get(0).isRemember()) {
                                    if (updateSettings(mapSettingsEntities.get(0), mapSettings)) {
                                        sendBroadcast(new Intent(Constants.HTTP_SERVICE_NAME).putExtra(IntentAttributes.HTTP_SETTINGS_RESPONSE_TYPE, Constants.WARN));
                                    }
                                }
                            } else {
                                if (mapSettings.size() == 1) {
                                    updateSettings(new MapSettingsEntity(), mapSettings);
                                } else {
                                    sendBroadcast(new Intent(Constants.HTTP_SERVICE_NAME).putExtra(IntentAttributes.HTTP_SETTINGS_RESPONSE_TYPE, Constants.NEED_UPDATE));
                                }
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
                .subscribe(new Subscriber<List<Run>>() {
                    public void onCompleted() {
                        sendBroadcast(new Intent(Constants.HTTP_SERVICE_NAME).putExtra(IntentAttributes.HTTP_JOBS_RESPONSE_TYPE, Constants.STOP));
                    }

                    public void onError(Throwable e) {
                        MCLoggerFactory.getLogger(LoginActivity.class).error(e.getMessage(), e);
                        sendBroadcast(new Intent(Constants.HTTP_SERVICE_NAME).putExtra(IntentAttributes.HTTP_JOBS_RESPONSE_TYPE, Constants.ERROR));
                    }

                    public void onNext(List<Run> runs) {
                        sendBroadcast(new Intent(Constants.HTTP_SERVICE_NAME).putExtra(IntentAttributes.HTTP_JOBS_RESPONSE_TYPE, Constants.START));
                        List<JobEntity> jobs = new LinkedList<>();
                        for (Run run : runs) {
                            jobs.add(SingleJobRenderer.renderJob(run));
                        }
                        ServicesRegistry.getDataController().clear();
                        for (JobEntity job : jobs) {
                            ServicesRegistry.getDataController().updateJob(job);
                        }
                    }
                });
    }

    private void getRoute() {
    }
}