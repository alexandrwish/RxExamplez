package com.magenta.mc.client.android.ui.activity;

import android.app.Activity;
import android.os.Bundle;

import com.magenta.mc.client.android.R;

/**
 * Splash activity. Your can add your own resource.
 * !!! add android:theme="@android:style/Theme.Black.NoTitleBar.Fullscreen to this activity
 * definition in AndroidManifest to hide title bar
 */
public abstract class SplashActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_splash);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(getVisibilityTime());
                } catch (InterruptedException ignore) {
                } finally {
                    finish();
                    startLoginActivity();
                }
            }
        }).start();
    }

    protected abstract void startLoginActivity();

    protected int getVisibilityTime() {
        return 3000;
    }
}