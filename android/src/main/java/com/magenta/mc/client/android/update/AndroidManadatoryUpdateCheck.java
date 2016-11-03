package com.magenta.mc.client.android.update;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: const
 * Date: 27.02.13
 * Time: 13:15
 * To change this template use File | Settings | File Templates.
 */
public class AndroidManadatoryUpdateCheck extends AbstractAndroidUpdateCheck {

    public AndroidManadatoryUpdateCheck(final Context context) {
        super(context);
    }

    protected Intent createUpdateIntent() {
        final Intent updateIntent = new Intent("android.intent.action.VIEW");
        updateIntent.setDataAndType(Uri.fromFile(getUpdateFile()), "application/vnd.android.package-archive");
        updateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        updateIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return updateIntent;
    }
}
