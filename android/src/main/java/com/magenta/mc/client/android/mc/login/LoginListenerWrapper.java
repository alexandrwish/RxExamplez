package com.magenta.mc.client.android.mc.login;

public class LoginListenerWrapper implements LoginListener {

    private LoginListener[] listeners;

    public LoginListenerWrapper(LoginListener[] listeners) {
        this.listeners = listeners;
    }

    public void loginFailed(String error) {
        for (LoginListener listener : listeners) {
            listener.loginFailed(error);
        }
    }

    public void loginSuccess(boolean initiatedByUser) {
        for (LoginListener listener : listeners) {
            listener.loginSuccess(initiatedByUser);
        }
    }

    public void loginMessage(String msg) {
        for (LoginListener listener : listeners) {
            listener.loginMessage(msg);
        }
    }

    public void bindResource(String myJid) {
        for (LoginListener listener : listeners) {
            listener.bindResource(myJid);
        }
    }
}