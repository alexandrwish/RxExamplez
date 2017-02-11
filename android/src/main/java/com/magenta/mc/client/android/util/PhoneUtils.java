package com.magenta.mc.client.android.util;

import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PhoneUtils {

    private final static View.OnClickListener ON_CLICK_LISTENER = new View.OnClickListener() {
        public void onClick(final View v) {
            v.getContext().startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + v.getTag())));
        }
    };

    private PhoneUtils() {
    }

    public static void assignPhone(final TextView field, final String phone) {
        if (StringUtils.isBlank(phone)) {
            field.setTag(null);
            field.setText(null);
            field.setOnClickListener(null);
        } else {
            field.setTag(phone);
            field.setText(Html.fromHtml("<u>" + phone + "</u>"));
            field.setOnClickListener(ON_CLICK_LISTENER);
        }
    }

    public static void assignPhone(final ImageView field, final String phone) {
        if (StringUtils.isBlank(phone)) {
            field.setTag(null);
            field.setVisibility(View.GONE);
            field.setOnClickListener(null);
        } else {
            field.setTag(phone);
            field.setVisibility(View.VISIBLE);
            field.setOnClickListener(ON_CLICK_LISTENER);
        }
    }
}