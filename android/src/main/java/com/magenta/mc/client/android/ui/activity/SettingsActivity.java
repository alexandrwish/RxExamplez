package com.magenta.mc.client.android.ui.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.magenta.mc.client.android.McAndroidApplication;
import com.magenta.mc.client.android.ui.fragment.SettingsFragment;

public class SettingsActivity extends PreferenceActivity {

    protected void onCreate(Bundle savedInstanceState) {
        McAndroidApplication.getInstance().getThemeManager().applyThemeOnCreate(this);
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }
}