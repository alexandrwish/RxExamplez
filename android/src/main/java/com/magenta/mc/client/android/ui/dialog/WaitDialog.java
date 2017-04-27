package com.magenta.mc.client.android.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class WaitDialog extends DistributionDialogFragment {

    protected AlertDialog mDialog;
    protected boolean mWait;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        setCancelable(bundle.getBoolean(DialogFactory.CANCELABLE));
        mDialog = ProgressDialog.show(getActivity(), null, getActivity().getString(bundle.getInt(DialogFactory.VALUE)));
        if (bundle.getBoolean(DialogFactory.AUTO_KILL, true)) {
            Observable.just(0).subscribeOn(Schedulers.newThread()).observeOn(Schedulers.io()).delay(10, TimeUnit.SECONDS).subscribe(new Action1<Integer>() {
                public void call(Integer integer) {
                    if (mWait && getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                close(false);
                            }
                        });
                    }
                }
            });
        }
        mWait = true;
        return mDialog;
    }

    public void close(Boolean success) {
        if (mCallback != null) {
            mCallback.done(success);
        }
        mWait = false;
        mCallback = null;
        mDialog.dismiss();
    }

    public String getName() {
        return "Wait dialog";
    }
}