package com.magenta.mc.client.android.http;

import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.logger.LoggerFactory;
import com.magenta.hdmate.mx.ApiClient;
import com.magenta.hdmate.mx.api.MateApi;
import com.magenta.hdmate.mx.auth.ApiKeyAuth;
import com.magenta.hdmate.mx.model.OrderAction;
import com.magenta.hdmate.mx.model.OrderActionResult;
import com.magenta.hdmate.mx.model.Run;
import com.magenta.hdmate.mx.model.TelemetryRecord;
import com.magenta.mc.client.android.common.Constants;
import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.db.dao.DistributionDAO;
import com.magenta.mc.client.android.entity.Address;
import com.magenta.mc.client.android.entity.LocationEntity;
import com.magenta.mc.client.android.log.MCLoggerFactory;
import com.magenta.mc.client.android.record.LoginRecord;
import com.magenta.mc.client.android.record.LoginResultRecord;
import com.magenta.mc.client.android.record.PointsResultRecord;
import com.magenta.mc.client.android.resender.ResendableMetadata;
import com.magenta.mc.client.android.resender.Resender;
import com.magenta.mc.client.android.util.MxAndroidUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class HttpClient {

    private static final ResendableMetadata STATUS_RESENDABLE_METADATA = new ResendableMetadata("status");

    private static HttpClient instance;
    private final ServiceClient serviceClient;
    private MateApi apiClient;

    private HttpClient() {
        serviceClient = new Retrofit.Builder()
                .baseUrl("https://maxoptra.com")
                .client(new OkHttpClient.Builder().connectTimeout(5, TimeUnit.MINUTES).readTimeout(5, TimeUnit.MINUTES).build())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build().create(ServiceClient.class);
    }

    public static HttpClient getInstance() {
        if (instance == null) {
            instance = new HttpClient();
        }
        return instance;
    }

    public void init() {
        String port = Settings.get().getPort();
        ApiClient client = new ApiClient().defaultAdapter(("443".equals(port) ? "https://" : "http://") + Settings.get().getHost() + ":" + port + Constants.MX_MATE_POSTFIX);
        client.addAuthorization("api_key", new ApiKeyAuth("header", "sessionId"));
        client.setApiKey(com.magenta.mc.client.android.common.Settings.get().getAuthToken());
        apiClient = client.createService(MateApi.class);
    }

    public Observable<LoginResultRecord> login(String account, String login, String password) {
        LoginRecord record = new LoginRecord();
        record.setAccountTechName(account);
        record.setUsername(login);
        record.setPassword(password);
        record.setMd5Password(true);
        return serviceClient.login(getAddress(Constants.LOGIN_POSTFIX), record);
    }

    public Observable<PointsResultRecord> getRoute(List<Address> addresses) {
        Double[][] doubles = new Double[addresses.size()][];
        for (int i = 0; i < addresses.size(); i++) {
            doubles[i] = new Double[]{addresses.get(i).getLongitude(), addresses.get(i).getLatitude()};
        }
        String address = getAddress(Constants.GIS_POSTFIX) + "?" + Constants.PARAM_LOCATIONS + "=" + new Gson().toJson(doubles);
        return serviceClient.updateRoute(address, Constants.CONTENT_TYPE_VALUE, com.magenta.mc.client.android.common.Settings.get().getAuthToken());
    }

    private String getAddress(String postfix) {
        String port = Settings.get().getPort();
        return ("443".equals(port) ? "https://" : "http://") + Settings.get().getHost() + ":" + port + postfix;
    }

    private MateApi getApiClient() {
        if (apiClient == null) {
            init();
        }
        return apiClient;
    }

    public Observable<com.magenta.hdmate.mx.model.Settings> getSettings() {
        return getApiClient().registerLogin(Settings.get().getAuthToken(), MxAndroidUtil.getImei()).observeOn(Schedulers.io());
    }

    public Observable<List<Run>> getJobs() {
        return getApiClient().allscheduleGet(Settings.get().getAuthToken()).observeOn(Schedulers.io());
    }

    @Deprecated
    // TODO: 2/17/17 fix me
    public void sendState(final long id, String userId, String states, Map values) {
        if (values.get("stop-ref") == null) {
            Resender.getInstance().sent(STATUS_RESENDABLE_METADATA, id);
            return; //doesn't support run states
        }
        List<OrderAction> results = new ArrayList<>(1);
        OrderAction result = new OrderAction();
        result.setOrderId(Long.valueOf((String) values.get("stop-ref"), Character.MAX_RADIX));
        result.setPerformer(userId);
        result.setAction(states);
        result.setParameters(values);
        result.setUid(UUID.randomUUID().toString());
        result.setActionTime(Long.valueOf((String) values.get("date")));
        results.add(result);
        // TODO: 2/20/17 return observer
        getApiClient().actionPost((String) values.get(Constants.AUTH_TOKEN), results)
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<OrderActionResult>>() {
                    public void onCompleted() {

                    }

                    public void onError(Throwable e) {
                        MCLoggerFactory.getLogger(HttpClient.class).error(e.getMessage(), e);
                    }

                    public void onNext(List<OrderActionResult> orderActionResults) {
                        if (orderActionResults != null) {
                            for (OrderActionResult result : orderActionResults) {
                                if (result.getSuccess()) {
                                    Resender.getInstance().sent(STATUS_RESENDABLE_METADATA, id);
                                }
                            }
                        }
                    }
                });
    }

    @Deprecated
    // TODO: 2/17/17 fix me
    public void sendLocations(List<LocationEntity> locations) {
        HashMap<String, Pair<List<TelemetryRecord>, List<LocationEntity>>> records = new HashMap<>();
        for (LocationEntity entity : locations) {
            TelemetryRecord record = new TelemetryRecord();
            record.setDate(System.currentTimeMillis());
            record.setHeading(0D);
            record.setSpeed(entity.getSpeed().doubleValue());
            record.setLatitude(entity.getLat());
            record.setLongitude(entity.getLon());
            record.setTimestamp(entity.getDate());
            record.setBattery(entity.getBattery());
            record.setGprs(entity.getGprs());
            record.setGps(entity.getGps());
            if (!records.containsKey(entity.getToken())) {
                records.put(entity.getToken(), new Pair<List<TelemetryRecord>, List<LocationEntity>>(new LinkedList<TelemetryRecord>(), new LinkedList<LocationEntity>()));
            }
            records.get(entity.getToken()).first.add(record);
            records.get(entity.getToken()).second.add(entity);
        }
        for (final Map.Entry<String, Pair<List<TelemetryRecord>, List<LocationEntity>>> entry : records.entrySet()) {
            getApiClient().telemetryPost(entry.getKey(), entry.getValue().first)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<Boolean>() {
                        public void onCompleted() {

                        }

                        public void onError(Throwable e) {
                            MCLoggerFactory.getLogger(HttpClient.class).error(e.getMessage(), e);
                        }

                        public void onNext(Boolean success) {
                            if (success) {
                                try {
                                    DistributionDAO.getInstance().deleteLocations(entry.getValue().second);
                                } catch (SQLException e) {
                                    LoggerFactory.getLogger(HttpClient.class).error(e.getMessage(), e);
                                }
                            }
                        }
                    });
        }
    }
}