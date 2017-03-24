package com.magenta.mc.client.android.ui;

import com.magenta.mc.client.android.components.dialogs.manager.IDialogManager;

/**
 * Created by IntelliJ IDEA.
 * User: const
 * Date: 13.12.11
 * Time: 19:47
 * To change this template use File | Settings | File Templates.
 */
public interface UI {

    void toFront();

    IDialogManager getDialogManager();

    void shutdown();

}
