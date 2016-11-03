package com.magenta.mc.client.android.update;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * @author Sergey Grachev
 */
public class AndroidUpdateCheck extends AbstractAndroidUpdateCheck {


    public AndroidUpdateCheck(Context context) {
        super(context);
    }

    @Override
    protected Intent createUpdateIntent() {
        final Intent updateIntent = new Intent("android.intent.action.VIEW");
        updateIntent.setDataAndType(
                Uri.fromFile(getContext().getFileStreamPath(getUpdateFileName())),
                "application/vnd.android.package-archive");
        return updateIntent;
    }

//    @Override
//    public boolean checkDownloadedUpdate() {
//        return false;
//    }
}
