package com.magenta.mc.client.android.mc.components.dialogs;

/**
 * Created 14.10.2010
 *
 * @author Konstantin Pestrikov
 */

/**
 * done() exectutes in MobileApp.mainThreadPool
 */
public interface DialogCallback {
    void done(boolean ok);
}
