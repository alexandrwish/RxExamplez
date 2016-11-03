package com.magenta.mc.client.android.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;

import java.util.ArrayList;

/**
 * Project: mobile
 * Author:  Alexey Osipov
 * Created: 13.01.14
 * <p/>
 * Copyright (c) 1999-2014 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 */
public class AlertDialogFragment extends DialogFragment {

    private static final String ARG_ID = "arg_id";
    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_MSG = "arg_msg";
    private static final String ARG_BTN = "arg_btn";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getArguments().getString(ARG_TITLE))
                .setMessage(getArguments().getString(ARG_MSG))
                .setCancelable(false);

        ArrayList<EDAlertDialogButton> buttons = getArguments().getParcelableArrayList(ARG_BTN);
        if (buttons != null) {
            for (EDAlertDialogButton btn : buttons) {
                switch (btn.getType()) {
                    case POSITIVE:
                        builder.setPositiveButton(btn.getText(), new FragmentDialogClickListener(this, btn.getType()));
                        break;
                    case NEGATIVE:
                        builder.setNegativeButton(btn.getText(), new FragmentDialogClickListener(this, btn.getType()));
                        break;
                    case NEUTRAL:
                        builder.setNeutralButton(btn.getText(), new FragmentDialogClickListener(this, btn.getType()));
                        break;
                }
            }
        }

        return builder.create();
    }

    public static class Builder {
        private String id;
        private String title;
        private String msg;
        private ArrayList<EDAlertDialogButton> buttons = new ArrayList<EDAlertDialogButton>();

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder setNeutralButton(String text) {
            buttons.add(new EDAlertDialogButton(DialogButtonType.NEUTRAL, text));
            return this;
        }

        public Builder setPositiveButton(String text) {
            buttons.add(new EDAlertDialogButton(DialogButtonType.POSITIVE, text));
            return this;
        }

        public Builder setNegativeButton(String text) {
            buttons.add(new EDAlertDialogButton(DialogButtonType.NEGATIVE, text));
            return this;
        }

        public AlertDialogFragment create() {
            Bundle args = new Bundle();
            args.putString(ARG_ID, id);
            args.putString(ARG_TITLE, title);
            args.putString(ARG_MSG, msg);
            args.putParcelableArrayList(ARG_BTN, buttons);

            AlertDialogFragment dialogFragment = new AlertDialogFragment();
            dialogFragment.setArguments(args);
            return dialogFragment;
        }
    }

    private static class EDAlertDialogButton implements Parcelable {
        private DialogButtonType type;
        private String text;

        EDAlertDialogButton(DialogButtonType type, String text) {
            this.type = type;
            this.text = text;
        }

        public DialogButtonType getType() {
            return type;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(text);
            parcel.writeInt(type.ordinal());
        }
    }

    private static class FragmentDialogClickListener implements DialogInterface.OnClickListener {

        private DialogButtonType type;
        private DialogFragment fragment;

        private FragmentDialogClickListener(DialogFragment fragment, DialogButtonType type) {
            this.fragment = fragment;
            this.type = type;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            DialogFragmentCallback callback = DialogFragmentManager.getParent(DialogFragmentCallback.class, fragment);
            if (callback != null) {
                callback.onClick(fragment.getArguments().getString(ARG_ID), type);
            }
        }
    }
}
