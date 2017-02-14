package com.magenta.mc.client.android.mc.components.dialogs;

/**
 * @author: Petr Popov
 * Created: 14.10.11 13:24
 */
public interface IMCDialog {

    void show();

    void hide();

    void hideAndDispose();

    void setCallback(DialogCallback callback);

    boolean confirm();

    boolean isVisible();

    void setVisible(boolean visible);

    void toFront();

    void show(boolean inDaemonThread);
}
