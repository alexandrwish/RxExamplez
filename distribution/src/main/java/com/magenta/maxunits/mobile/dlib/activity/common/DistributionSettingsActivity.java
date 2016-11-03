package com.magenta.maxunits.mobile.dlib.activity.common;

import com.magenta.maxunits.distribution.R;
import com.magenta.maxunits.mobile.dlib.view.SettingsCustomView;

public class DistributionSettingsActivity extends com.magenta.maxunits.mobile.activity.common.SettingsActivity {

    protected void setAdditionalProperties() {
        super.setAdditionalProperties();
        addPreferencesFromResource(R.xml.preferences_distribution);
        getListView().addFooterView(new SettingsCustomView(DistributionSettingsActivity.this));
    }
}