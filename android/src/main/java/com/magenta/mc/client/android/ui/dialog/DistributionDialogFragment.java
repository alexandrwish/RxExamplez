package com.magenta.mc.client.android.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.log.MCLogger;
import com.magenta.mc.client.android.log.MCLoggerFactory;

public class DistributionDialogFragment extends DialogFragment {

    protected final MCLogger LOG = MCLoggerFactory.getLogger(getName());
    protected DialogCallback mCallback;

    public DistributionDialogFragment() {
    }

    public static DistributionDialogFragment newInstance(Class<? extends DistributionDialogFragment> cls, Bundle bundle) throws IllegalAccessException, java.lang.InstantiationException {
        DistributionDialogFragment dialog = cls.newInstance();
        dialog.setArguments(bundle);
        return dialog;
    }

    public String getName() {
        return "DistributionDialogFragment";
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setNeutralButton(R.string.mx_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                });
        int tmp;
        if ((tmp = bundle.getInt(DialogFactory.ICON)) > 0) {
            builder.setIcon(tmp);
        }
        if ((tmp = bundle.getInt(DialogFactory.TITLE)) > 0) {
            builder.setTitle(tmp);
        }
        if ((tmp = bundle.getInt(DialogFactory.VALUE)) > 0) {
            builder.setMessage(tmp);
        }
        return builder.create();
    }

    public void setCallback(DialogCallback callback) {
        mCallback = callback;
    }
}