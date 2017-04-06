package com.magenta.mc.client.android.common;

public interface Constants {

    String HTTP_SERVICE_NAME = "HTTP_SERVICE";
    String SENDER_SERVICE_NAME = "SENDER_SERVICE";

    String CONTENT_TYPE_VALUE = "application/json; charset=UTF-8";
    String CONTENT_TYPE = "Content-Type";
    String AUTH_HEADER = "X-Auth-Token";
    String AUTH_TOKEN = "api_key";

    String PARAM_LOCATIONS = "locations";

    String MX_MATE_POSTFIX = "/mx-mate-service/rest/";
    String LOGIN_POSTFIX = "/auth-service/rest/login/";
    String GIS_POSTFIX = "/gis-service/rest/routing/getRoute";
    String SOCKET_IO_POSTFIX = "/pda.io";

    String ADDRESS_LIST = "address.list";
    String SYNCHRONIZE_TIMESTAMP = "synchronize.timestamp";

    int LOGIN_TYPE = 1;
    int SETTINGS_TYPE = 2;
    int JOBS_TYPE = 3;
    int ROUTE_TYPE = 4;

    int OK = 1;
    int WARN = 2;
    int ERROR = 3;
    int NEED_UPDATE = 4;
    int START = 1;
    int STOP = 2;

    long SEND_DELTA = 60000L;
}