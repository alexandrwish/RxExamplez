package com.magenta.mc.client.android;

import com.google.inject.Module;
import com.magenta.mc.client.android.ui.theme.ThemeManageable;
import com.magenta.mc.client.android.ui.theme.ThemeManager;

public abstract class SmokeApplication extends McAndroidApplication implements ThemeManageable, ThemeManager.ThemeManagerListener {

    private ThemeManager themeManager;

    public void onCreate() {
        super.onCreate();
        themeManager = createThemeManager();
        themeManager.setListener(this);
    }

    protected Module createModule() {
        return new SmokeModule();
    }

    public abstract ThemeManager createThemeManager();

    public ThemeManager getThemeManager() {
        return themeManager;
    }

    public void onChangeTheme() {
        setTheme(themeManager.getCurrentThemeId(null));
    }
}