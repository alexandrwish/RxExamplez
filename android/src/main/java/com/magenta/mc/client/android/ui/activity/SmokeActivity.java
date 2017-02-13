package com.magenta.mc.client.android.ui.activity;

import android.app.Application;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.ui.delegate.SmokeActivityDelegate;
import com.magenta.mc.client.android.ui.theme.ThemeManageable;
import com.magenta.mc.client.android.ui.theme.ThemeManager;

public abstract class SmokeActivity<D extends SmokeActivityDelegate> extends GenericActivity<D> implements SmokeActivityInterface {

    protected void onCreate(Bundle savedInstanceState) {
        configureAppearance(false);
        super.onCreate(savedInstanceState);
        getDelegate().onCreate(savedInstanceState);
    }

    protected void onResume() {
        configureAppearance(true);
        super.onResume();
    }

    protected void configureAppearance(boolean onResume) {
        Application application = getApplication();
        if (application instanceof ThemeManageable) {
            ThemeManager themeManager = ((ThemeManageable) application).getThemeManager();
            if (themeManager != null) {
                if (onResume) {
                    themeManager.checkThemeOnResume(this);
                } else {
                    themeManager.applyThemeOnCreate(this);
                }
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (getMenu() != null) {
            return getDelegate().onCreateOptionMenu(menu);
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }

    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getDelegate().onCreateContextMenu(menu, v, menuInfo);
    }

    public void setTitle(CharSequence title) {
        if (title != null) {
            final TextView field = (TextView) findViewById(R.id.mcTitleBarText);
            if (field != null) {
                field.setText(title);
            } else {
                super.setTitle(title);
            }
        }
    }

    public boolean isHasTitleBar() {
        return true;
    }

    public Integer getTitleBarLeftMenu() {
        return null;
    }

    public Integer getTitleBarRightMenu() {
        return null;
    }

    public Integer getMenu() {
        return null;
    }

    public String getCustomTitle() {
        return null;
    }

    public boolean isVisible() {
        return getDelegate().isVisible();
    }
}