package com.magenta.mc.client.android.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.setup.Setup;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        PreferenceGroup rootPreferenceGroup = getPreferenceScreen();
        for (int i = 0; i < rootPreferenceGroup.getPreferenceCount(); i++) {
            Preference preference = rootPreferenceGroup.getPreference(i);
            if (preference instanceof PreferenceGroup) {
                PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
                for (int j = 0; j < preferenceGroup.getPreferenceCount(); j++) {
                    fillFieldForPreference(preferenceGroup.getPreference(j));
                }
            } else {
                fillFieldForPreference(preference);
            }
        }
    }

    private void fillFieldForPreference(Preference preference) {
        Settings settings = Setup.get().getSettings();
        String value = settings.getProperty(preference.getKey());
        if (preference instanceof EditTextPreference) {
            EditTextPreference textPreference = (EditTextPreference) preference;
            textPreference.setText(value);
            textPreference.setSummary(value);
        } else if (preference instanceof CheckBoxPreference) {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
            checkBoxPreference.setChecked("true".equals(value));
        } else if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            listPreference.setSummary(listPreference.getEntry());
        }
    }

    private void updateSummary(final String key) {
        final Preference preference = getPreferenceScreen().findPreference(key);
        if (preference != null) {
            CharSequence summary;
            if (preference instanceof ListPreference) {
                summary = ((ListPreference) preference).getEntry();
            } else {
                summary = Setup.get().getSettings().getProperty(key);
            }
            preference.setSummary(summary);
        }
    }

    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        Setup.get().getSettings().saveSettings();
    }

    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        Object value = sharedPreferences.getAll().get(key);
        if (value != null) {
            Setup.get().getSettings().setProperty(key, value.toString());
            if (!(value instanceof Boolean)) {
                updateSummary(key);
            }
        }
    }
}