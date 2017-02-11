package com.magenta.mc.client.android.http;

import com.magenta.mc.client.android.http.record.LoginRecord;
import com.magenta.mc.client.android.http.record.LoginResultRecord;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface LoginClient {

    @POST
    Call<LoginResultRecord> login(@Url String url, @Body LoginRecord record);
}