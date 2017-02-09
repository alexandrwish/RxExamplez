package com.magenta.maxunits.mobile.dlib.dialog;

import android.os.Bundle;

import com.magenta.mc.client.log.MCLogger;
import com.magenta.mc.client.log.MCLoggerFactory;

public class DialogFactory {

    public static final int ALERT_DIALOG = 0;
    public static final int MAP_CHOOSER_DIALOG = 1;
    public static final String ICON = "icon";
    public static final String TITLE = "title";
    public static final String VALUE = "value";
    private static final MCLogger LOG = MCLoggerFactory.getLogger(DialogFactory.class);

    private DialogFactory() {
    }

    public static DistributionDialogFragment create(int code, Bundle bundle) {
        try {
            switch (code) {
                case MAP_CHOOSER_DIALOG: {
                    return DistributionDialogFragment.newInstance(MapChooserDialog.class, bundle);
                }
                case ALERT_DIALOG: {
                    return DistributionDialogFragment.newInstance(DistributionDialogFragment.class, bundle);
                }
                default:
                    return null;
            }
        } catch (IllegalAccessException | InstantiationException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }
}