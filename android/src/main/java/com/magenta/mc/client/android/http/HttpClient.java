package com.magenta.mc.client.android.http;

import android.preference.PreferenceManager;

import com.google.gson.GsonBuilder;
import com.magenta.hdmate.mx.ApiClient;
import com.magenta.hdmate.mx.api.MateApi;
import com.magenta.hdmate.mx.auth.ApiKeyAuth;
import com.magenta.hdmate.mx.model.SettingsResultRecord;
import com.magenta.mc.client.android.DistributionApplication;
import com.magenta.mc.client.android.common.Constants;
import com.magenta.mc.client.android.http.record.LoginRecord;
import com.magenta.mc.client.android.http.record.LoginResultRecord;
import com.magenta.mc.client.android.mc.MxAndroidUtil;
import com.magenta.mc.client.android.mc.MxSettings;
import com.magenta.mc.client.settings.Settings;

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
        ApiClient client = new ApiClient().defaultAdapter(("443".equals(port) ? "https://" : "http://") + MxSettings.get().getProperty(Settings.HOST) + ":" + port + Constants.SCHEDULE_MT_POSTFIX);
        client.addAuthorization("api_key", new ApiKeyAuth("header", "sessionId"));
        client.setApiKey(PreferenceManager.getDefaultSharedPreferences(DistributionApplication.getInstance()).getString(Constants.AUTH_TOKEN, ""));
        apiClient = client.createService(MateApi.class);
    }

    public Call<LoginResultRecord> login(String account, String login, String password) {
        String port = "80"; // TODO: 2/6/17 impl
        String address = ("443".equals(port) ? "https://" : "http://") + MxSettings.get().getProperty(Settings.HOST) + ":" + port + Constants.LOGIN_POSTFIX;
        LoginRecord record = new LoginRecord();
        record.setAccountTechName(account);
        record.setUsername(login);
        record.setPassword(password);
        record.setMd5Password(false);
        return loginClient.login(address, record);
    }

    public Observable<SettingsResultRecord> getSettings() {
        return apiClient.registerLogin(MxAndroidUtil.getImei())
                .observeOn(Schedulers.io());
    }
}