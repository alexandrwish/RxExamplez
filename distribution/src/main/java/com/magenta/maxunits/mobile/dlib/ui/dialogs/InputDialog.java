package com.magenta.maxunits.mobile.dlib.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;

import com.magenta.maxunits.distribution.R;

public final class InputDialog {

    public static void showPasswordInput(final String title, final String message, final Callback<String> callback, final Context ctx) {
        showTextInput(title, message, callback, ctx, true);
    }

    private static void showTextInput(final String title, final String message, final Callback<String> callback, final Context ctx, final boolean isPassword) {
        final EditText editText = new EditText(ctx);
        editText.setSingleLine(true);
        if (isPassword) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        new AlertDialog.Builder(ctx)
                .setTitle(title)
                .setMessage(message)
                .setView(editText)
                .setPositiveButton(ctx.getText(R.string.mx_ok), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int whichButton) {
                        if (callback != null) {
                            callback.ok(editText.getText().toString());
                        }
                    }
                })
                .setNegativeButton(ctx.getText(R.string.mx_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int whichButton) {
                    }
                })
                .show();
    }

    public interface Callback<V> {

        void ok(V value);

        void cancel();
    }
}