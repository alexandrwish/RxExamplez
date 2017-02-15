package com.magenta.mc.client.android.mc.login;

public interface LoginListener {

    void loginFailed(String error);

    void loginSuccess(boolean initiatedByUser);

    void loginMessage(String msg);

    void bindResource(String myJid);
}
