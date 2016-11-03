package com.magenta.maxunits.mobile.dlib.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.magenta.maxunits.distribution.R;
import com.magenta.mc.client.log.MCLogger;
import com.magenta.mc.client.log.MCLoggerFactory;

public class DistributionDialogFragment extends DialogFragment {

    protected MCLogger LOG = MCLoggerFactory.getLogger(getName());

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
}