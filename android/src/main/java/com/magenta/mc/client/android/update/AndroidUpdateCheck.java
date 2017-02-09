package com.magenta.mc.client.android.update;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class AndroidUpdateCheck extends AbstractAndroidUpdateCheck {

    public AndroidUpdateCheck(Context context) {
        super(context);
    }

    protected Intent createUpdateIntent() {
        final Intent updateIntent = new Intent("android.intent.action.VIEW");
        updateIntent.setDataAndType(Uri.fromFile(getContext().getFileStreamPath(getUpdateFileName())), "application/vnd.android.package-archive");
        return updateIntent;
    }
}