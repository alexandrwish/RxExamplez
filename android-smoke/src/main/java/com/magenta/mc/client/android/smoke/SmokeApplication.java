package com.magenta.mc.client.android.smoke;

import com.google.inject.Module;
import com.magenta.mc.client.android.McAndroidApplication;
import com.magenta.mc.client.android.ui.theme.ThemeManageable;
import com.magenta.mc.client.android.ui.theme.ThemeManager;

/**
 * Project: Santa-cruz
 * Author:  Alexandr Komarov
 * Created: 18.12.13 15:10
 * <p/>
 * Copyright (c) 1999-2013 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 * <p/>
 * $Id$
 */
public abstract class SmokeApplication extends McAndroidApplication implements ThemeManageable, ThemeManager.ThemeManagerListener {

    private ThemeManager themeManager;

    @Override
    public void onCreate() {
        super.onCreate();
        themeManager = createThemeManager();
        themeManager.setListener(this);
    }

    @Override
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
