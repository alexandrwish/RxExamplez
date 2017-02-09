package com.magenta.maxunits.mobile.dlib.mc;

import com.magenta.maxunits.mobile.dlib.DistributionApplication;
import com.magenta.maxunits.mobile.dlib.rpc.DistributionRPCOut;
import com.magenta.maxunits.mobile.dlib.service.SaveLocationService;
import com.magenta.maxunits.mobile.dlib.service.storage.DemoStorageInitializerImpl;
import com.magenta.maxunits.mobile.dlib.xmpp.XMPPStream2;
import com.magenta.maxunits.mobile.dlib.rpc.RPCTarget;
import com.magenta.maxunits.mobile.dlib.service.ServicesRegistry;
import com.magenta.mc.client.client.ConnectionListener;
import com.magenta.mc.client.client.Login;
import com.magenta.mc.client.client.TimeSynchronization;
import com.magenta.mc.client.client.resend.Resender;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.xmpp.XMPPStream;
import com.magenta.mc.client.xmpp.extensions.rpc.JabberRPC;

public class DistributionApp extends MxMobile {

    public DistributionApp() {
        super(new String[]{}, DistributionApplication.getContext(), new DemoStorageInitializerImpl());
    }

    public XMPPStream initStream(String serverName, String host, int port, boolean ssl, long connectionId) throws java.io.IOException {
        return new XMPPStream2(serverName, host, port, ssl, connectionId);
    }

    protected void initUncaughtExceptionHandler() {
        //UncaughtExceptionHandler больше не нужен - исключения ловит ACRA
    }

    protected void setupLoginListener() {
        final Login.Listener loginListener = Login.getInstance().getListener();
        Login.getInstance().setListener(new Login.Listener() {
            public void fail() {
                if (loginListener != null) {
                    loginListener.fail();
                }
            }

            public void successBeforeWake(boolean initiatedByUser) {
                if (loginListener != null) {
                    loginListener.successBeforeWake(initiatedByUser);
                }
                ServicesRegistry.getDataController().init();
                Resender.getInstance().loadCacheIfNecessary();
            }

            public void successAfterWake(boolean initiatedByUser) {
                if (!Setup.get().getSettings().isOfflineVersion()) {
                    TimeSynchronization.synchronize();
                    ConnectionListener.getInstance().connected();
                }
            }

            public void afterLogout() {
                Login.setUserLoggout();
            }
        });
    }

    protected void setupGeoLocationAPI() {
        ServicesRegistry.startSaveLocationsService(DistributionApplication.getInstance(), SaveLocationService.class);
    }

    protected void stopGeoLocationAPI() {
        ServicesRegistry.stopSaveLocationsService();
    }

    protected void initSetup() {
        super.initSetup();
        MxSettings.getInstance().enableFeature(MxSettings.Features.ACCOUNT);
    }

    protected void setupRPCListeners() {
        JabberRPC.getInstance().setListener(RPCTarget.getInstance());
        JabberRPC.getInstance().setHandler(DistributionRPCOut.getInstance());
    }
}