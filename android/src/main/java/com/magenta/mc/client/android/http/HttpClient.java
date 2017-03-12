package com.magenta.mc.client.android.http;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.magenta.hdmate.mx.ApiClient;
import com.magenta.hdmate.mx.api.MateApi;
import com.magenta.hdmate.mx.auth.ApiKeyAuth;
import com.magenta.hdmate.mx.model.Job;
import com.magenta.hdmate.mx.model.OrderAction;
import com.magenta.hdmate.mx.model.OrderActionResult;
import com.magenta.hdmate.mx.model.TelemetryRecord;
import com.magenta.mc.client.android.McAndroidApplication;
import com.magenta.mc.client.android.common.Constants;
import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.entity.Address;
import com.magenta.mc.client.android.mc.MxAndroidUtil;
import com.magenta.mc.client.android.mc.client.resend.ResendableMetadata;
import com.magenta.mc.client.android.mc.client.resend.Resender;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.tracking.GeoLocation;
import com.magenta.mc.client.android.mc.tracking.GeoLocationBatch;
import com.magenta.mc.client.android.record.LoginRecord;
import com.magenta.mc.client.android.record.LoginResultRecord;
import com.magenta.mc.client.android.record.PointsResultRecord;
import com.magenta.mc.client.android.rpc.RPCOut;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
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

    public Observable<com.magenta.hdmate.mx.model.Settings> getSettings() {
        return apiClient.registerLogin(MxAndroidUtil.getImei()).observeOn(Schedulers.io());
    }

    public Observable<List<Job>> getJobs() {
        return apiClient.allscheduleGet().observeOn(Schedulers.io());
    }

    @Deprecated
    // TODO: 2/17/17 fix me
    public void sendState(final long id, String userId, String jobRef, String states, Map values) {
        List<OrderAction> results = new ArrayList<>(1);
        OrderAction result = new OrderAction();
        result.setOrderId(Long.valueOf((String) values.get("stop-ref"), Character.MAX_RADIX));
        result.setPerformer(userId);
        result.setAction(states);
        result.setParameters(values);
        result.setUid(UUID.randomUUID().toString());
        try {
            result.setActionTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.UK).parse((String) values.get("date")));
        } catch (ParseException e) {
            result.setActionTime(new Date());
        }
        results.add(result);
        // TODO: 2/20/17 return observer
        apiClient.actionPost(results)
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<OrderActionResult>>() {
                    public void onCompleted() {

                    }

                    public void onError(Throwable e) {
                        MCLoggerFactory.getLogger(RPCOut.class).error(e.getMessage(), e);
                    }

                    public void onNext(List<OrderActionResult> orderActionResults) {
                        Resender.getInstance().sent(STATUS_RESENDABLE_METADATA, id);
                    }
                });
    }

    @Deprecated
    // TODO: 2/17/17 fix me
    public void sendLocations(final Long id, List<GeoLocation> locations) {
        List<TelemetryRecord> records = new LinkedList<>();
        for (GeoLocation o : locations) {
            TelemetryRecord record = new TelemetryRecord();
            record.setDate(System.currentTimeMillis());
            record.setHeading(o.getHeading().doubleValue());
            record.setSpeed(o.getSpeed().doubleValue());
            record.setLatitude(o.getLat());
            record.setLongitude(o.getLon());
            record.setTimestamp(o.getRetrieveTimestamp());
            record.setBattery(getBatteryLevel());
            record.setGprs(getNetworkInfo());
            record.setGps(isGPSEnable());
        }
        // TODO: 2/20/17 return observer
        apiClient.telemetryPost(records)
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Boolean>() {
                    public void onCompleted() {

                    }

                    public void onError(Throwable e) {
                        MCLoggerFactory.getLogger(HttpClient.class).error(e.getMessage(), e);
                    }

                    public void onNext(Boolean aBoolean) {
                        Resender.getInstance().sent(GeoLocationBatch.METADATA, id);
                    }
                });
    }

    // TODO: 2/20/17 use EasyDeviceInfo
    private Double getBatteryLevel() {
        Intent batteryIntent = McAndroidApplication.getInstance().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batteryIntent == null) {
            return -1d;
        }
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        if (level == -1 || scale == -1) {
            return -1d;
        }
        return ((float) level / (float) scale) * 100.0d;
    }

    private boolean isGPSEnable() {
        LocationManager manager = (LocationManager) McAndroidApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);
        return manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private String getNetworkInfo() {
        ConnectivityManager connectivityManager = (ConnectivityManager) McAndroidApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null ? activeNetworkInfo.toString() : "[]";
    }
}