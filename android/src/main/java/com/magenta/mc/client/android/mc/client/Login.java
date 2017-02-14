package com.magenta.mc.client.android.mc.client;

import com.magenta.mc.client.android.MobileApp;
import com.magenta.mc.client.android.mc.components.dialogs.DialogCallback;
import com.magenta.mc.client.android.mc.components.waiting.WaitIcon;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.login.AuthFactory;
import com.magenta.mc.client.android.mc.login.LoginListener;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.mc.tracking.GeoLocationService;
import com.magenta.mc.client.android.mc.util.FutureRunnable;
import com.magenta.mc.client.android.mc.util.McDiagnosticAgent;
import com.magenta.mc.client.android.rpc.xmpp.XmppError;

public class Login implements LoginListener {

    private static boolean loginSuccess;
    private static boolean loginFailReported;
    private static boolean userLoggedIn = false;
    private static Login instance;
    private static Runnable wakeUp;
    private static boolean logout = false;

    private Listener listener = new Listener() {

        public void fail() {
        }

        public void successBeforeWake(boolean initiatedByUser) {
            userLoggedIn = true;
        }

        public void successAfterWake(boolean initiatedByUser) {
            if (!Setup.get().getSettings().isOfflineVersion()) {
                TimeSynchronization.synchronize();
                ConnectionListener.getInstance().connected();
                GeoLocationService.getInstance().start(true);
            }
        }

        public void afterLogout() {
            userLoggedIn = false;
            GeoLocationService.getInstance().stop(true);
        }
    };

    public static Login getInstance() {
        if (instance == null) {
            instance = new Login();
        }
        return instance;
    }

    public static boolean isUserLoggedIn() {
        return userLoggedIn;
    }

    public static void setUserLoggout() {
        userLoggedIn = false;
    }

    public static void login(final String userId, final String pin, DialogCallback dialogHidingCallback) {
        MCLoggerFactory.getLogger().trace("Login.login()");
        WaitIcon.show(new FutureRunnable() {
            public void run(Runnable future) {
                if (Setup.get().getSettings().isOfflineVersion()) {
                    Setup.get().getSettings().setUserId("demo_storage");
                    if (Setup.get().getStorageInitializer().initStorage()) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            //do noting
                        }
                    } else {
                        Setup.get().getUI().getDialogManager().asyncMessageSafe("Error", "error while loading storage from demo_storage.zip");
                    }
                    boolean initiatedByUser = XMPPClient.getInstance().isTryToLogin();
                    Login.getInstance().getListener().successBeforeWake(initiatedByUser);
                    Login.getInstance().getListener().successAfterWake(initiatedByUser);
                    future.run();
                } else {
                    if (MobileApp.isRunningInTestMode() && Setup.get().getSettings().needToInitializeStorage()) {
                        Setup.get().getStorageInitializer().initStorage();
                    }
                    getInstance().doLogin(userId, pin, future);
                }
            }
        }, dialogHidingCallback);
    }

    public static void wake() {
        if (wakeUp != null) {
            final Runnable wakeUpRef = wakeUp;
            wakeUp = null;
            wakeUpRef.run();
        }
    }

    public static boolean isLogout() {
        return logout;
    }

    private void showLoginAuthFailedMessage() {
        final String message = MobileApp.localize("auth.failed");
        Setup.get().getUI().getDialogManager().asyncMessageSafe(MobileApp.localize("Error"), message);
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void doLogin(final String userId, final String pin, final Runnable future) {
        wakeUp = new Runnable() {
            public void run() {
                if (!loginSuccess && !loginFailReported) {
                    // we seem to have waken up by timeout, stop everything and fail
                    ConnectionListener.stopConnecting();
                    XMPPClient.getInstance().stop();
                    loginFailReported = true;
                    Login.getInstance().reloginIfNeeded(userId, pin, future);
                } else {
                    future.run();
                }
            }
        };
        logout = false;
        Setup.get().getSettings().setPassword(pin);
        if (!AuthFactory.getListeners().contains(this)) {
            AuthFactory.addListener(this);
        }
        loginSuccess = false;
        loginFailReported = false;
        XMPPClient.getInstance().stop();
        XMPPClient.getInstance().start();
        ConnectionListener.startConnecting();
    }

    private void reloginIfNeeded(String userId, String pin, Runnable future) {
        WaitIcon.setShowingPaused(true);
        if (Setup.get().getUI().getDialogManager().confirmSafe(MobileApp.localize("connection.failed"), MobileApp.localize("connection.failed.reconnect"))) {
            doLogin(userId, pin, future);
            WaitIcon.setShowingPaused(false);
        } else {
            future.run();
        }
    }

    public boolean logout() {
        return logout(false);
    }

    public boolean logout(boolean initiatedByUser) {
        if (initiatedByUser) {
            DriverStatus.OFFLINE.set(new UserPressStatusExtender(true));
        }
        logout = true;
        Setup.get().getSettings().setPassword(null);
        XMPPClient.getInstance().stop();
        if (listener != null) {
            listener.afterLogout();
        }
        McDiagnosticAgent.getInstance().signalLogout();
        return true;
    }

    public void loginFailed(String error) {
        MCLoggerFactory.getLogger(getClass()).info("Login.loginFailed()");
        if (Login.isUserLoggedIn() && Setup.get().getSettings().getIntProperty("reconnect.count", "3") < 0) {
            // application is set up to reconnect infinitely, and user has already logged in
            // let it reconnect on timer
        } else {
            if (XmppError.NOT_AUTHORIZED_TEXT.equals(error) && XMPPClient.getInstance().isTryToLogin()) {
                showLoginAuthFailedMessage();
                loginFailReported = true;
            }
            loginSuccess = false;
            ConnectionListener.stopConnecting();
            XMPPClient.getInstance().stop();
            if (listener != null) {
                listener.fail();
            }
        }
    }

    public void loginSuccess(final boolean initiatedByUser) {
        MCLoggerFactory.getLogger(getClass()).info("Login.loginSuccess()");
        MobileApp.runTask(new Runnable() {

            public void run() {
                loginSuccess = true;
                if (listener != null) {
                    listener.successBeforeWake(initiatedByUser);
                }
                ConnectionListener.stopConnecting();
                wake();
                if (listener != null) {
                    listener.successAfterWake(initiatedByUser);
                }
            }
        });
    }

    public void loginMessage(String msg) {
        // todo: show message?
    }

    public void bindResource(String myJid) {
        // todo: what?
    }

    public interface Listener {

        void fail();

        void successBeforeWake(boolean initiatedByUser);

        void successAfterWake(boolean initiatedByUser);

        void afterLogout();
    }
}