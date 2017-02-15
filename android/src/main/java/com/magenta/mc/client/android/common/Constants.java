package com.magenta.mc.client.android.common;

public interface Constants {

    String HTTP_SERVICE_NAME = "HTTP_SERVICE";
    String LOGIN_POSTFIX = "/auth-service/rest/login/";
    String MX_MATE_POSTFIX = "/mx-mate-service/rest/";
    String AUTH_TOKEN = "api_key";

    int LOGIN_TYPE = 1;
    int SETTINGS_TYPE = 2;
    int JOBS_TYPE = 3;

    int OK = 1;
    int WARN = 2;
    int ERROR = 3;

    int START = 1;
    int STOP = 2;
}