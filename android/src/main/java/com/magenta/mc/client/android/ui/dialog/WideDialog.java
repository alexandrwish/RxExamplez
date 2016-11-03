package com.magenta.mc.client.android.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.AbsListView;

/**
 * @autor Petr Popov
 * Created 07.06.12 17:16
 */
public class WideDialog extends Dialog {

    private int layout;

    public WideDialog(int layout, Context context) {
        super(context);
        this.layout = layout;
    }

    public WideDialog(int layout, Context context, int theme) {
        super(context, theme);
        this.layout = layout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = AbsListView.LayoutParams.FILL_PARENT;
        getWindow().setAttributes(params);
    }
}
