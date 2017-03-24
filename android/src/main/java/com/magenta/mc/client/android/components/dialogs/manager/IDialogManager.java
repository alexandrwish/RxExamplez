package com.magenta.mc.client.android.components.dialogs.manager;

import com.magenta.mc.client.android.components.dialogs.DialogCallback;

public interface IDialogManager {

    void asyncMessageSafe(String title, String msg);

    void asyncMessageSafe(String title, String msg, DialogCallback callback);

    void showDialogsAgain(Object o);
}