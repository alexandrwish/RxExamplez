package com.magenta.mc.client.android.acra;

import com.magenta.mc.client.android.DistributionApplication;
import com.magenta.mc.client.android.mc.MxAndroidUtil;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;

import org.acra.ACRA;
import org.acra.ACRAConfiguration;

public class AcraConfigurator {

    public void init(DistributionApplication application) {
        try {
            ACRAConfiguration config = ACRA.getNewDefaultConfig(application);
            config.setSharedPreferenceName("acra");
            ACRA.setConfig(config);
            ACRA.init(application);
            ACRA.getErrorReporter().putCustomData("IMEI", MxAndroidUtil.getImei());
            ACRA.getErrorReporter().putCustomData("Application Name: ", application.getName());
            ACRA.getErrorReporter().addReportSender(new AcraLoggerSender());
            ACRA.getErrorReporter().addReportSender(new AcraSentrySender());
        } catch (Exception e) {
            MCLoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
    }
}