package com.magenta.maxunits.mobile.dlib.acra;

import android.net.Uri;

import com.magenta.maxunits.mobile.dlib.http.HTTPHelper;
import com.magenta.maxunits.mobile.mc.MxSettings;
import com.magenta.mc.client.log.MCLoggerFactory;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

public class SentryHelper {

    private static final String DEFAULT_BASE_URL = "https://app.getsentry.com";
    private static final String VERSION = "0.2.0";

    public void captureEvent(final SentryEventBuilder builder) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Map<String, Object> requestBody = builder.event;
                Uri dsn = getDsn();
                if (dsn != null) {
                    Map<String, String> headers = new HashMap<String, String>(3);
                    headers.put("X-Sentry-Auth", createXSentryAuthHeader(dsn));
                    headers.put("User-Agent", "sentry-android");
                    headers.put("Content-Type", "text/html; charset=utf-8");
                    String url = DEFAULT_BASE_URL + "/api/" + getProjectId(dsn) + "/store/";
                    HTTPHelper httpHelper = new HTTPHelper();
                    httpHelper.postJson(url, requestBody, headers);
                }
            }

        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private Uri getDsn() {
        String dsnStr = MxSettings.getInstance().getProperty(MxSettings.SENTRY_DSN);
        if (dsnStr.isEmpty()) {
            IllegalStateException e = new IllegalStateException();
            MCLoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
            return null;
        }
        return Uri.parse(dsnStr);
    }

    private String createXSentryAuthHeader(Uri dsn) {
        String authority = dsn.getAuthority().replace("@" + dsn.getHost(), "");
        String[] authorityParts = authority.split(":");
        String publicKey = authorityParts[0];
        String secretKey = authorityParts[1];
        String header = "";
        header += "Sentry sentry_version=7,";
        header += "sentry_client=sentry-android/" + VERSION + ",";
        header += "sentry_timestamp=" + System.currentTimeMillis() + ",";
        header += "sentry_key=" + publicKey + ",";
        header += "sentry_secret=" + secretKey;
        return header;
    }

    private String getProjectId(Uri dsn) {
        String path = dsn.getPath();
        return path.substring(path.lastIndexOf("/") + 1);
    }

    public static class SentryEventBuilder {

        final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.UK);

        static {
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        Map<String, Object> event;

        public SentryEventBuilder() {
            event = new HashMap<String, Object>();
            event.put("event_id", UUID.randomUUID().toString().replace("-", ""));
            event.put("platform", "java");
            this.setTimestamp(System.currentTimeMillis());
        }

        public SentryEventBuilder setMessage(String message) {
            event.put("message", message);
            return this;
        }

        public SentryEventBuilder setTimestamp(long timestamp) {
            event.put("timestamp", sdf.format(new Date(timestamp)));
            return this;
        }

        public SentryEventBuilder setLevel(SentryEventLevel level) {
            event.put("level", level.value);
            return this;
        }

        public SentryEventBuilder setCulprit(String culprit) {
            event.put("culprit", culprit);
            return this;
        }

        public SentryEventBuilder setExtra(Map<String, String> extra) {
            setExtra(new JSONObject(extra));
            return this;
        }

        public SentryEventBuilder setExtra(JSONObject extra) {
            event.put("extra", extra);
            return this;
        }

        public enum SentryEventLevel {

            FATAL("fatal"),
            ERROR("error"),
            WARNING("warning"),
            INFO("info"),
            DEBUG("debug");

            String value;

            SentryEventLevel(String value) {
                this.value = value;
            }
        }
    }
}