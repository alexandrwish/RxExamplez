package com.magenta.mc.client.android.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.magenta.mc.client.android.common.Constants;
import com.magenta.mc.client.android.http.HttpClient;
import com.magenta.mc.client.android.http.record.LoginResultRecord;
import com.magenta.mc.client.android.util.IntentAttributes;
import com.magenta.mc.client.log.MCLoggerFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HttpService extends IntentService {

    public HttpService() {
        super(Constants.HTTP_SERVICE_NAME);
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
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            preferences.edit().putString(Constants.AUTH_TOKEN, result.getToken()).apply();
                            sendBroadcast(new Intent(Constants.HTTP_SERVICE_NAME).putExtra(IntentAttributes.HTTP_RESPONSE_TYPE, Constants.OK)); //все хорошо
                        } else {
                            sendBroadcast(new Intent(Constants.HTTP_SERVICE_NAME).putExtra(IntentAttributes.HTTP_RESPONSE_TYPE, Constants.WARN)); //что-то пошло не так
                        }
                    }

                    public void onFailure(Call<LoginResultRecord> call, Throwable t) {
                        MCLoggerFactory.getLogger(HttpService.class).error(t.getMessage(), t);
                        sendBroadcast(new Intent(Constants.HTTP_SERVICE_NAME).putExtra(IntentAttributes.HTTP_RESPONSE_TYPE, Constants.ERROR)); //все плохо
                    }
                });
    }

    private void getSettings() {
        HttpClient.getInstance().getSettings();
    }
}