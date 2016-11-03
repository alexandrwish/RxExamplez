package com.magenta.mc.client.android.smoke.activity;

import android.app.Application;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.magenta.mc.client.android.smoke.R;
import com.magenta.mc.client.android.smoke.activity.delegate.SmokeActivityDelegate;
import com.magenta.mc.client.android.ui.activity.GenericActivity;
import com.magenta.mc.client.android.ui.theme.ThemeManageable;
import com.magenta.mc.client.android.ui.theme.ThemeManager;

/**
 * @autor Sergey Grachev
 * @autor Petr Popov
 * Created 14.05.12 16:30
 */

public abstract class SmokeActivity extends GenericActivity implements SmokeActivityInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        configureAppearance(false);

        super.onCreate(savedInstanceState);

        getDelegate().onCreate(savedInstanceState);
    }

    @Override
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getMenu() != null) {
            return getDelegate().onCreateOptionMenu(menu);
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getDelegate().onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
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

    @Override
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

    @Override
    public SmokeActivityDelegate getDelegate() {
        return (SmokeActivityDelegate) super.getDelegate();
    }

    public boolean isVisible() {
        return getDelegate().isVisible();
    }
}