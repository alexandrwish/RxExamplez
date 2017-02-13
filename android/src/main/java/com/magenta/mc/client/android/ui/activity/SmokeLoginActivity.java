package com.magenta.mc.client.android.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.ui.AndroidUI;
import com.magenta.mc.client.android.ui.delegate.SmokeActivityDelegate;
import com.magenta.mc.client.client.Login;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.update.UpdateCheck;

public abstract class SmokeLoginActivity<D extends SmokeActivityDelegate> extends SmokeActivity<D> {

    protected TextView version;
    Button settingsButton;
    Button loginButton;

    public boolean isHasTitleBar() {
        return false;
    }

    public void initActivity(Bundle savedInstanceState) {
        UpdateCheck updateCheck = Setup.get().getUpdateCheck();
        if (updateCheck.checkDownloadedUpdate()) {
            updateCheck.installDownloadedUpdate();
        }
        setContentView(R.layout.screen_login);
        loginButton = (Button) findViewById(R.id.login_button);
        settingsButton = (Button) findViewById(R.id.settings_button);
        version = (TextView) findViewById(R.id.version_view);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setCurrentActivity();
                processLogin();
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setCurrentActivity();
                processSettingsButtonClick();
            }
        });
    }

    protected abstract void setCurrentActivity();

    protected abstract void processSettingsButtonClick();

    protected abstract void processLogin();

    private void switchToCurrentActivityIfNecessary() {
        MCLoggerFactory.getLogger(getClass()).debug("Switch to current activity if necessary");
        if (Setup.isInitialized()) {
            Activity currentActivity = chooseNextActivity();
            String activityClass = currentActivity != null ? currentActivity.getClass().getSimpleName() : "null";
            MCLoggerFactory.getLogger(getClass()).debug("LoginActivity " + activityClass + " " + Login.isUserLoggedIn());
            if (currentActivity != null
                    && !(currentActivity instanceof SmokeLoginActivity)
                    && Login.isUserLoggedIn()) {

                Intent intent = new Intent(SmokeLoginActivity.this, currentActivity.getClass());
                intent.putExtra("FROM_LOGIN_ACTIVITY", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                finish();
                startActivity(intent);
            }
        } else {
            MCLoggerFactory.getLogger(getClass()).debug("Setup is not initialized, can't get current activity.");
        }
    }

    protected Activity chooseNextActivity() {
        return ((AndroidUI) Setup.get().getUI()).getCurrentActivity();
    }

    public void onBackPressed() {
        moveTaskToBack(true);
    }

    protected void onResume() {
        super.onResume();
        switchToCurrentActivityIfNecessary();
    }
}