package com.magenta.mc.client.android.ui.delegate;

import com.magenta.mc.client.android.common.Constants;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;

public class WorkflowDelegate extends HDDelegate {

    public void jobsResult(int result) {
        MCLoggerFactory.getLogger(getClass()).debug("Jobs result = " + result);
        switch (result) {
            case Constants.START: {
                break;
            }
            case Constants.ERROR: {
                break;
            }
            case Constants.STOP: {
                break;
            }
        }
    }
}
