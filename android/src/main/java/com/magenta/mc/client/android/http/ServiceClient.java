package com.magenta.mc.client.android.http;

import com.magenta.mc.client.android.common.Constants;
import com.magenta.mc.client.android.record.LoginRecord;
import com.magenta.mc.client.android.record.LoginResultRecord;
import com.magenta.mc.client.android.record.PointsResultRecord;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;

public interface ServiceClient {

    @POST
    Observable<LoginResultRecord> login(@Url String url, @Body LoginRecord record);

    @GET
    Observable<PointsResultRecord> updateRoute(@Url String url, @Header(Constants.CONTENT_TYPE) String contentType, @Header(Constants.AUTH_HEADER) String authToken);
}