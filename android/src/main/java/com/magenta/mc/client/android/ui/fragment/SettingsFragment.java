package com.magenta.mc.client.android.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.j256.ormlite.logger.LoggerFactory;
import com.magenta.mc.client.android.McAndroidApplication;
import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.db.dao.TileCacheDAO;
import com.magenta.mc.client.android.util.LocaleUtils;
import com.magenta.mc.client.android.util.ThemeUtils;

import java.sql.SQLException;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        fillPreferences(getPreferenceScreen());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout v = (LinearLayout) super.onCreateView(inflater, container, savedInstanceState);
        if (v == null) {
            return null;
        }
        Preference button = findPreference(getString(R.string.clear_cache));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                try {
                    TileCacheDAO.getInstance().removeCacheTiles(System.currentTimeMillis());
                    Toast.makeText(getActivity(), R.string.clear_cache_msg, Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
                }
                return true;
            }
        });
        return v;
    }

    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    protected void fillPreferences(PreferenceGroup rootPreferenceGroup) {
        for (int i = 0; i < rootPreferenceGroup.getPreferenceCount(); i++) {
            Preference preference = rootPreferenceGroup.getPreference(i);
            if (preference instanceof PreferenceGroup) {
                fillPreferences((PreferenceGroup) preference);
            } else if (preference instanceof EditTextPreference) {
                if (preference.getKey().equalsIgnoreCase(Settings.SETTING_PASSWORD)) {
                    String s = ((EditTextPreference) preference).getText();
                    if (s != null) {
                        preference.setSummary(s.replaceAll("\\d", "*"));
                    }
                } else {
                    preference.setSummary(((EditTextPreference) preference).getText());
                }
            } else if (preference instanceof ListPreference) {
                if (Settings.APP_LOCALE.equals(preference.getKey())) {
                    ((ListPreference) preference).setEntries(LocaleUtils.listOfAvailableLocales(McAndroidApplication.getInstance()));
                }
                preference.setSummary(((ListPreference) preference).getEntry());
            }
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Object value = sharedPreferences.getAll().get(key);
        if (value != null && !(value instanceof Boolean)) {
            Preference preference = getPreferenceScreen().findPreference(key);
            if (preference != null) {
                if (preference instanceof ListPreference) {
                    preference.setSummary(((ListPreference) preference).getEntry());
                } else if (preference instanceof EditTextPreference) {
                    if (preference.getKey().equalsIgnoreCase(Settings.SETTING_PASSWORD)) {
                        String s = ((EditTextPreference) preference).getText();
                        preference.setSummary(s.replaceAll("\\d", "*"));
                    } else {
                        preference.setSummary(((EditTextPreference) preference).getText());
                    }
                }
            }
            if (Settings.APP_THEME.equals(key)) {
                switchUiTheme(sharedPreferences);
            } else if (Settings.APP_LOCALE.equals(key)) {
                switchLang(sharedPreferences);
            }
        }
    }

    private void switchLang(SharedPreferences sharedPreferences) {
        LocaleUtils.changeLocale(McAndroidApplication.getInstance(), sharedPreferences.getString(Settings.APP_LOCALE, null));
        Activity activity = getActivity();
        startActivity(new Intent(activity, activity.getClass()));
        activity.finish();
    }

    private void switchUiTheme(SharedPreferences sharedPreferences) {
        Activity activity = getActivity();
        ThemeUtils.switchToTheme(Integer.valueOf(sharedPreferences.getString(Settings.APP_THEME, "0")));
        activity.finish();
        startActivity(new Intent(activity, activity.getClass()));
    }
}