package com.magenta.mc.client.android.ui.theme;

import android.app.Activity;
import android.content.Intent;
import android.view.ContextThemeWrapper;

import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.settings.AndroidSettings;

import java.util.HashMap;
import java.util.Map;

public abstract class ThemeManager {

    public static final Theme DEFAULT_THEME = Theme.NIGHT;
    private Theme currentTheme;
    private ThemeManagerListener listener;
    private Map<String, Theme> activityThemeCache = new HashMap<>();

    public Theme getCurrentTheme() {
        if (currentTheme == null) {
            if (Setup.isInitialized()) {
                AndroidSettings settings = (AndroidSettings) Setup.get().getSettings();
                currentTheme = settings.getApplicationTheme();
            } else {
                return DEFAULT_THEME;
            }
        }
        return currentTheme;
    }

    public void applyThemeOnCreate(Activity activity) {
        activity.setTheme(getCurrentThemeId(activity));
        putToCache(activity);
    }

    public void checkThemeOnResume(Activity activity) {
        if (getFromCache(activity) != getCurrentTheme()) {
            applyThemeOnResumedActivity(activity);
        }
    }

    private void applyThemeOnResumedActivity(Activity activity) {
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    private void putToCache(Activity activity) {
        String key = activity.getClass().getSimpleName();
        activityThemeCache.put(key, getCurrentTheme());
    }

    private Theme getFromCache(Activity activity) {
        String key = activity.getClass().getSimpleName();
        return activityThemeCache.get(key);
    }

    public void switchToTheme(Theme currentTheme) {
        this.currentTheme = currentTheme;
        if (listener != null) {
            listener.onChangeTheme();
        }
    }

    public int getCurrentThemeId(ContextThemeWrapper context) {
        return getThemeId(context, getCurrentTheme());
    }

    public void setListener(ThemeManagerListener listener) {
        this.listener = listener;
    }

    protected abstract int getThemeId(ContextThemeWrapper context, Theme theme);

    public interface ThemeManagerListener {
        void onChangeTheme();
    }
}