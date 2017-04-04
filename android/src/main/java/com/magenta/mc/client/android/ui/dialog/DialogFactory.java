package com.magenta.mc.client.android.ui.dialog;

import android.os.Bundle;

import com.magenta.mc.client.android.log.MCLogger;
import com.magenta.mc.client.android.log.MCLoggerFactory;

public class DialogFactory {

    public static final int ALERT_DIALOG = 0;
    public static final int MAP_CHOOSER_DIALOG = 1;
    public static final int WAIT_DIALOG = 2;
    public static final String ICON = "icon";
    public static final String TITLE = "title";
    public static final String VALUE = "value";
    public static final String AUTO_KILL = "kill";
    public static final String CANCELABLE = "cancelable";
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
                case WAIT_DIALOG: {
                    return DistributionDialogFragment.newInstance(WaitDialog.class, bundle);
                }
                default:
                    return null;
            }
        } catch (IllegalAccessException e) {
            LOG.error(e.getMessage(), e);
        } catch (InstantiationException t) {
            LOG.error(t.getMessage(), t);
        }
        return null;
    }
}