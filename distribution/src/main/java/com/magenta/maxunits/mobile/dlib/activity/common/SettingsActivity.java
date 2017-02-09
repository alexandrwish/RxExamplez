package com.magenta.maxunits.mobile.dlib.activity.common;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;

import com.magenta.maxunits.distribution.R;
import com.magenta.maxunits.mobile.dlib.MxApplication;
import com.magenta.maxunits.mobile.dlib.mc.MxSettings;
import com.magenta.maxunits.mobile.dlib.utils.LocaleUtils;
import com.magenta.maxunits.mobile.dlib.utils.StringUtils;
import com.magenta.maxunits.mobile.dlib.view.SettingsCustomView;
import com.magenta.mc.client.android.settings.AndroidSettings;
import com.magenta.mc.client.android.SmokeApplication;
import com.magenta.mc.client.android.ui.theme.Theme;
import com.magenta.mc.client.android.ui.theme.ThemeManager;
import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.setup.Setup;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    protected static final String UI_THEME_KEY = "ui.theme";

    protected void onCreate(final Bundle savedInstanceState) {
        ((SmokeApplication) getApplication()).getThemeManager().applyThemeOnCreate(this);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setAdditionalProperties();
        PreferenceGroup rootPreferenceGroup = getPreferenceScreen();
        for (int i = 0; i < rootPreferenceGroup.getPreferenceCount(); i++) {
            Preference preference = rootPreferenceGroup.getPreference(i);
            if (preference instanceof PreferenceGroup) {
                PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
                for (int j = 0; j < preferenceGroup.getPreferenceCount(); j++) {
                    Preference p = preferenceGroup.getPreference(j);
                    if (canAddSettingOnUI(p)) {
                        fillFieldForPreference(p);
                    } else {
                        preferenceGroup.removePreference(p);
                    }
                }
            } else {
                if (canAddSettingOnUI(preference)) {
                    fillFieldForPreference(preference);
                } else {
                    rootPreferenceGroup.removePreference(preference);
                }
            }
        }
    }

    private boolean canAddSettingOnUI(Preference preference) {
        if (preference != null) {
            String key = preference.getKey();
            if (key != null && !key.isEmpty()) {
                return !((MxApplication) getApplication()).getHiddenSettings().contains(key);
            }
        }
        return true;
    }

    private void fillFieldForPreference(Preference preference) {
        Settings settings = Setup.get().getSettings();
        String value = settings.getProperty(preference.getKey());
        final boolean isBlank = StringUtils.isBlank(value);
        if (preference instanceof EditTextPreference) {
            EditTextPreference textPreference = (EditTextPreference) preference;
            if (MxSettings.SETTING_PASSWORD.equals(preference.getKey())) {
                textPreference.setText(isBlank ? "" : "   ");
                textPreference.setSummary(getString(isBlank ? R.string.mx_unlocked : R.string.mx_locked));
            } else {
                textPreference.setText(value);
                textPreference.setSummary(value);
            }
        } else if (preference instanceof CheckBoxPreference) {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
            checkBoxPreference.setChecked("true".equals(value));
        } else if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            if (Settings.LOCALE_KEY.equals(preference.getKey())) {
                listPreference.setEntries(LocaleUtils.listOfAvailableLocales(getApplication()));
                listPreference.setValue(value);
            }
            listPreference.setSummary(listPreference.getEntry());
        }
    }

    protected void setAdditionalProperties() {
        addPreferencesFromResource(com.magenta.maxunits.distribution.R.xml.preferences_distribution);
        getListView().addFooterView(new SettingsCustomView(SettingsActivity.this));
    }

    private void updateSummary(final String key) {
        final Preference preference = getPreferenceScreen().findPreference(key);
        if (preference != null) {
            CharSequence summary;
            if (MxSettings.SETTING_PASSWORD.equals(preference.getKey())) {
                summary = getString(StringUtils.isBlank(Setup.get().getSettings().getProperty(key)) ? R.string.mx_unlocked : R.string.mx_locked);
            } else if (preference instanceof ListPreference) {
                summary = ((ListPreference) preference).getEntry();
            } else {
                summary = Setup.get().getSettings().getProperty(key);
            }
            preference.setSummary(summary);
        }
    }

    protected void onResume() {
        ((SmokeApplication) getApplication()).getThemeManager().checkThemeOnResume(this);
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
            final Preference preference = findPreference(key);
            Setup.get().getSettings().setProperty(key, value.toString());
            if (Settings.LOCALE_KEY.equals(key)) {
                LocaleUtils.changeLocale(getApplication(), String.valueOf(value));
                preference.setSummary(LocaleUtils.getDisplayName(String.valueOf(value)));
            } else if (!(value instanceof Boolean)) {
                updateSummary(key);
            }
            if (AndroidSettings.PROPERTY_UI_THEME.equals(key)) {
                switchUiTheme(sharedPreferences);
            }
        }
    }

    private void switchUiTheme(SharedPreferences sharedPreferences) {
        String currentThemeCode = sharedPreferences.getString(AndroidSettings.PROPERTY_UI_THEME, null);
        ThemeManager uiManager = ((SmokeApplication) getApplication()).getThemeManager();
        uiManager.switchToTheme(Theme.lookup(Integer.parseInt(currentThemeCode)));
        finish();
        startActivity(new Intent(this, getClass()));
    }
}