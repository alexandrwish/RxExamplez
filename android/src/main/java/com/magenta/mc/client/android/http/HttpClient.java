package com.magenta.mc.client.android.http;

import com.google.gson.GsonBuilder;
import com.magenta.hdmate.mx.ApiClient;
import com.magenta.hdmate.mx.api.MateApi;
import com.magenta.hdmate.mx.auth.ApiKeyAuth;
import com.magenta.hdmate.mx.model.JobRecord;
import com.magenta.hdmate.mx.model.OrderAction;
import com.magenta.hdmate.mx.model.OrderActionResult;
import com.magenta.hdmate.mx.model.SettingsResultRecord;
import com.magenta.mc.client.android.common.Constants;
import com.magenta.mc.client.android.mc.MxAndroidUtil;
import com.magenta.mc.client.android.mc.MxSettings;
import com.magenta.mc.client.android.mc.settings.Settings;
import com.magenta.mc.client.android.record.LoginRecord;
import com.magenta.mc.client.android.record.LoginResultRecord;
import com.magenta.mc.client.android.service.storage.entity.Stop;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

public class HttpClient {

    private static HttpClient instance;
    private final LoginClient loginClient;
    private MateApi apiClient;

    private HttpClient() {
        loginClient = new Retrofit.Builder()
                .baseUrl("https://maxoptra.com")
                .client(new OkHttpClient.Builder().connectTimeout(5, TimeUnit.MINUTES).readTimeout(5, TimeUnit.MINUTES).build())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .build().create(LoginClient.class);
    }

    public static HttpClient getInstance() {
        if (instance == null) {
            instance = new HttpClient();
        }
        return instance;
    }

    public void init() {
        String port = "80";
        ApiClient client = new ApiClient().defaultAdapter(("443".equals(port) ? "https://" : "http://") + MxSettings.get().getProperty(Settings.HOST) + ":" + port + Constants.MX_MATE_POSTFIX);
        client.addAuthorization("api_key", new ApiKeyAuth("header", "sessionId"));
        client.setApiKey(com.magenta.mc.client.android.common.Settings.get().getAuthToken());
        apiClient = client.createService(MateApi.class);
    }

    public Call<LoginResultRecord> login(String account, String login, String password) {
        String port = "80"; // TODO: 2/6/17 impl
        String address = ("443".equals(port) ? "https://" : "http://") + MxSettings.get().getProperty(Settings.HOST) + ":" + port + Constants.LOGIN_POSTFIX;
        LoginRecord record = new LoginRecord();
        record.setAccountTechName(account);
        record.setUsername(login);
        record.setPassword(password);
        record.setMd5Password(true);
        return loginClient.login(address, record);
    }

    public Observable<SettingsResultRecord> getSettings() {
        return apiClient.registerLogin(MxAndroidUtil.getImei()).observeOn(Schedulers.io());
    }

    public Observable<List<JobRecord>> getJobs() {
        return apiClient.allscheduleGet().observeOn(Schedulers.io());
    }

    @Deprecated
    // TODO: 2/17/17 fix me
    public Observable<List<OrderActionResult>> sendState(String userId, String jobRef, String states, Map values) {
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
        return apiClient.actionPost(results);
    }
}