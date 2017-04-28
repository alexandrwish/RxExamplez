package com.magenta.mc.client.android.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.magenta.mc.client.android.R;

public class AbortDialog extends Dialog {

    private final Listener listener;

    public AbortDialog(final Context context, final Listener listener) {
        super(context, R.style.McThemeDialog);
        this.listener = listener;
    }

    @SuppressWarnings("unchecked")
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mx_job_abort);
        findViewById(R.id.mxAbortButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                listener.onAbort(((TextView) findViewById(R.id.mxAbortReason)).getText().toString());
                dismiss();
            }
        });
        findViewById(R.id.mxCancelButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                cancel();
            }
        });
    }

    public interface Listener {
        void onAbort(String reason);
    }
}