package com.magenta.mc.client.android.common;

public interface Constants {

    String HTTP_SERVICE_NAME = "HTTP_SERVICE";
    String LOGIN_POSTFIX = "/auth-service/rest/login/";
    String SCHEDULE_MT_POSTFIX = "/schedule_mt/rest/";
    String AUTH_TOKEN = "api_key";

    int LOGIN_TYPE = 1;
    int SETTINGS_TYPE = 2;

    int OK = 1;
    int WARN = 2;
    int ERROR = 3;
}