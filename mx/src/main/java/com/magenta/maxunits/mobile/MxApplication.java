package com.magenta.maxunits.mobile;

import android.content.Context;
import android.content.res.Configuration;
import android.view.ContextThemeWrapper;

import com.magenta.maxunits.mobile.activity.common.SettingsActivity;
import com.magenta.maxunits.mobile.db.MxDBAdapter;
import com.magenta.maxunits.mobile.mc.MXNavApp;
import com.magenta.maxunits.mobile.mc.MxSettings;
import com.magenta.maxunits.mobile.utils.LocaleUtils;
import com.magenta.maxunits.mx.R;
import com.magenta.mc.client.android.service.McService;
import com.magenta.mc.client.android.smoke.SmokeApplication;
import com.magenta.mc.client.android.ui.theme.Theme;
import com.magenta.mc.client.android.ui.theme.ThemeManager;
import com.magenta.mc.client.settings.Settings;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public abstract class MxApplication extends SmokeApplication {

    protected static MxApplication instance;
    protected MxDBAdapter dbAdapter;
    protected MXNavApp mxNavApp;
    protected boolean loginPress;

    public static MxApplication getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance != null ? instance.getApplicationContext() : null;
    }

    public void onCreate() {
        super.onCreate();
        instance = this;
        initNavAppClient();
        initDBAdapter();
    }

    public void onTerminate() {
        if (mxNavApp != null) {
            this.mxNavApp.unregisterCurrentLocationListener();
            this.mxNavApp.destroy();
            this.mxNavApp = null;
        }
        super.onTerminate();
    }

    protected void initDBAdapter() {
        this.dbAdapter = new MxDBAdapter(this);
    }

    private void initNavAppClient() {
        try {
            this.mxNavApp = MXNavApp.init(this);
            this.mxNavApp.registerCurrentLocationListener();
        } catch (Exception ignore) {
            this.mxNavApp = null;
        }
    }

    public MxDBAdapter getDBAdapter() {
        return dbAdapter;
    }

    public MXNavApp getMxNavApp() {
        return mxNavApp;
    }

    protected void startMcService() {
        //don't start mc service here
    }

    protected Class<? extends McService> getServiceClass() {
        return null;
    }

    public ThemeManager createThemeManager() {
        return new MxThemeManager();
    }

    /**
     * Override this method if You want hide settings of your app.
     *
     * @return set of settings keys
     */
    public Set<String> getHiddenSettings() {
        Set<String> result = new HashSet<String>();
        result.add("ui.theme");
        return result;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleUtils.changeLocale(this, MxSettings.get().getProperty(Settings.LOCALE_KEY));
    }

    public void completeStatisticSending(Date date) {
    }

    public void checkHistory(Runnable runnable) {
    }

    public boolean isLoginPress() {
        return loginPress;
    }

    public void setLoginPress(boolean loginPress) {
        this.loginPress = loginPress;
    }

    private static class MxThemeManager extends ThemeManager {

        protected int getThemeId(ContextThemeWrapper context, Theme theme) {
            if (context != null && context instanceof SettingsActivity) {
                switch (theme) {
                    case day:
                        return R.style.McTheme_NoTitleBar_Light;
                    case night:
                        return R.style.McTheme_NoTitleBar;
                    default:
                        throw new IllegalArgumentException();
                }
            } else {
                switch (theme) {
                    case day:
                        return R.style.McTheme_Light;
                    case night:
                        return R.style.McTheme;
                    default:
                        throw new IllegalArgumentException();
                }
            }
        }
    }
}