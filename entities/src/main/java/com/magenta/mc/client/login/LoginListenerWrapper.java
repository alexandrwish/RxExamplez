package com.magenta.mc.client.login;

/**
 * Created 03.03.2010
 *
 * @author Konstantin Pestrikov
 */
public class LoginListenerWrapper implements LoginListener {
    LoginListener[] listeners;

    public LoginListenerWrapper(LoginListener[] listeners) {
        this.listeners = listeners;
    }

    public void loginFailed(String error) {
        for (int i = 0; i < listeners.length; i++) {
            LoginListener listener = listeners[i];
            listener.loginFailed(error);
        }
    }

    public void loginSuccess(boolean initiatedByUser) {
        for (int i = 0; i < listeners.length; i++) {
            LoginListener listener = listeners[i];
            listener.loginSuccess(initiatedByUser);
        }
    }

    public void loginMessage(String msg) {
        for (int i = 0; i < listeners.length; i++) {
            LoginListener listener = listeners[i];
            listener.loginMessage(msg);
        }
    }

    public void bindResource(String myJid) {
        for (int i = 0; i < listeners.length; i++) {
            LoginListener listener = listeners[i];
            listener.bindResource(myJid);
        }
    }
}
