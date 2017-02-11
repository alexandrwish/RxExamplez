package com.magenta.mc.client.android.mc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.magenta.mc.client.android.service.ServicesRegistry;
import com.magenta.mc.client.android.ui.activity.MxGenericActivity;
import com.magenta.mc.client.android.ui.activity.common.LoginActivity;
import com.magenta.mc.client.android.rpc.RPCOut;
import com.magenta.mc.client.android.rpc.RPCTarget;
import com.magenta.maxunits.mobile.entity.AbstractJobStatus;
import com.magenta.mc.client.android.AndroidApp;
import com.magenta.mc.client.android.ui.AndroidUI;
import com.magenta.mc.client.client.ConnectionListener;
import com.magenta.mc.client.client.DriverStatus;
import com.magenta.mc.client.client.Login;
import com.magenta.mc.client.client.Msg;
import com.magenta.mc.client.client.XMPPClient;
import com.magenta.mc.client.client.resend.ResendableMetadata;
import com.magenta.mc.client.client.resend.Resender;
import com.magenta.mc.client.demo.DemoStorageInitializer;
import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.xmpp.extensions.rpc.JabberRPC;

public class MxMobile extends AndroidApp {

    private final DemoStorageInitializer demoStorageInitializer;

    public MxMobile(String[] args, Context applicationContext, DemoStorageInitializer demoStorageInitializer) {
        super(args, applicationContext);
        this.demoStorageInitializer = demoStorageInitializer;
        instance = this;
        run();
        Settings.get().setProperty("offline.version", "false");
    }

    protected void initSetup() {
        Setup.init(new MxSetup(applicationContext, demoStorageInitializer));
    }

    protected void setupLoginListener() {
        final Login.Listener loginListener = Login.getInstance().getListener();
        Login.getInstance().setListener(new Login.Listener() {
            public void fail() {
                if (loginListener != null) {
                    loginListener.fail();
                }
            }

            public void successBeforeWake(final boolean initiatedByUser) {
                if (loginListener != null) {
                    loginListener.successBeforeWake(initiatedByUser);
                }
                ServicesRegistry.getDataController().init();
                Resender.getInstance().loadCacheIfNecessary();
                ServicesRegistry.getWorkflowService().showNextActivity(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            public void successAfterWake(final boolean initiatedByUser) {
                try { // sleep for a while to let hourglass dialog shutdown
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    //ok
                }
                if (loginListener != null) {
                    loginListener.successAfterWake(initiatedByUser);
                }
            }

            public void afterLogout() {
                if (loginListener != null) {
                    loginListener.afterLogout();
                }
                diagnosticRestart = false;
            }
        });
    }


    protected void setupConnectionListener() {
        final ConnectionListener.Listener listener = ConnectionListener.getInstance().getListener();
        ConnectionListener.getInstance().setListener(new ConnectionListener.Listener() {
            public void connected() {
                listener.connected();
                runTask(new Runnable() {
                    public void run() {
                        Setup.get().getUpdateCheck().check();
                    }
                });
                Activity currActivity = ((AndroidUI) Setup.get().getUI()).getCurrentActivity();
                if (currActivity instanceof MxGenericActivity) {
                    ((MxGenericActivity) currActivity).getDelegate().setDriverStatus(DriverStatus.ONLINE);
                }
                MxNotifications.showConnectionStatus(applicationContext, true, Setup.get().getSettings().getAppName());
            }

            public void disconnected() {
                listener.disconnected();
                Activity currActivity = ((AndroidUI) Setup.get().getUI()).getCurrentActivity();
                if (currActivity instanceof MxGenericActivity) {
                    ((MxGenericActivity) currActivity).getDelegate().setDriverStatus(DriverStatus.OFFLINE);
                }
                MxNotifications.showConnectionStatus(applicationContext, false, Setup.get().getSettings().getAppName());
            }
        });
    }

    protected void setupMessageListener() {
        XMPPClient.getInstance().setMsgListener(new XMPPClient.MsgListener() {
            public void incoming(final Msg msg) {
                Setup.get().getUI().getDialogManager().runAsyncDialogTask(new Runnable() {
                    public void run() {
                        Toast.makeText(((AndroidUI) Setup.get().getUI()).getCurrentActivity(), msg.getBody(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    protected void setupRPCListeners() {
        // set a jabber-rpc-listener for jobs receiving
        JabberRPC.getInstance().setListener(RPCTarget.getInstance());
        JabberRPC.getInstance().setHandler(RPCOut.getInstance());
    }

    protected ResendableMetadata[] getResendablesMetadata() {
        return joinMetadata(super.getResendablesMetadata(), new ResendableMetadata[]{AbstractJobStatus.RESENDABLE_METADATA});
    }

    protected void setupXmppConflictListener() {
        XMPPClient.getInstance().setXmppConflictListener(new Runnable() {
            public void run() {
                Login.setUserLoggout();
                applicationContext.startActivity(new Intent(applicationContext, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }
}