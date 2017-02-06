package com.magenta.maxunits.mobile.http;

import com.google.gson.GsonBuilder;
import com.magenta.hdmate.mx.ApiClient;
import com.magenta.maxunits.mobile.common.Constants;
import com.magenta.maxunits.mobile.http.record.LoginRecord;
import com.magenta.maxunits.mobile.http.record.LoginResultRecord;
import com.magenta.maxunits.mobile.mc.MxSettings;
import com.magenta.mc.client.settings.Settings;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpClient {

    private static HttpClient instance;
    private final ApiClient apiClient;
    private final LoginClient loginClient;

    private HttpClient() {
        apiClient = null;
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
}