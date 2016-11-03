package com.magenta.maxunits.mobile.task;

import android.content.SharedPreferences;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.magenta.maxunits.mobile.MxApplication;
import com.magenta.maxunits.mobile.mc.MxAndroidUtil;
import com.magenta.maxunits.mobile.mc.MxSettings;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.setup.Setup;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.util.Map;

public class GetRemoteSettings {

    private static final String DISTRIBUTION_HTTP_CLIENT = "distributionHttpClient";
    private static final int RESULT_OK = 0;
    private static final int RESULT_ERROR = 1;
    private HttpClient httpclient;
    private RemoteSettingsCallback mUpdSettingsCallback;
    private WaitDbUpdate mWaitDbUpdate;

    public GetRemoteSettings(RemoteSettingsCallback updSettingsCallback) {
        mUpdSettingsCallback = updSettingsCallback;
        httpclient = AndroidHttpClient.newInstance(DISTRIBUTION_HTTP_CLIENT, MxApplication.getInstance());
    }

    public void update() {
        if (mWaitDbUpdate != null) {
            mWaitDbUpdate.cancel(true);
        }
        mWaitDbUpdate = new WaitDbUpdate();
        mWaitDbUpdate.execute();
    }

    private String getPath() {
        return "http://" + Setup.get().getSettings().get(MxSettings.API_ADDRESS) + Setup.get().getSettings().get(MxSettings.API_PATH) + "/getSettings?account=" + MxSettings.getInstance().getUserAccount();
    }

    class WaitDbUpdate extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... params) {
            try {
                HttpResponse response = httpclient.execute(new HttpGet(getPath()));
                String result = MxAndroidUtil.readResponse(response.getEntity().getContent());
                if (!result.isEmpty()) {
                    Map<String, String> settings = new Gson().fromJson(result, Map.class);
                    if (settings.containsKey("errorCode")) {
                        MCLoggerFactory.getLogger(getClass()).error(settings.get("message"));
                        return RESULT_ERROR;
                    } else {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MxApplication.getContext()).edit();
                        for (Map.Entry<String, String> s : settings.entrySet()) {
                            Settings.get().setProperty(s.getKey(), s.getValue());
                            editor.putString(s.getKey(), s.getValue());
                        }
                        Settings.get().saveSettings();
                        editor.apply();
                        return RESULT_OK;
                    }
                }
            } catch (Exception e) {
                MCLoggerFactory.getLogger(getClass()).error(e.getStackTrace());
            }
            return RESULT_ERROR;
        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (isCancelled()) {
                return;
            }
            if (result == RESULT_OK) {
                mUpdSettingsCallback.getRemoteSettingsSuccess();
            } else {
                mUpdSettingsCallback.getRemoteSettingsError();
            }
        }
    }
}