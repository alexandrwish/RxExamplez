package com.magenta.mc.client.android.ui.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.magenta.mc.client.android.R;

public class MxTextView extends TextView {

    private static final String TAG = "MxTextView";

    public MxTextView(final Context context) {
        super(context);
    }

    public MxTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public MxTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(final Context ctx, final AttributeSet attrs) {
        final TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.MxTextView);
        final String customFont = a.getString(R.styleable.MxTextView_customTypeface);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(final Context ctx, final String asset) {
        try {
            setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/" + asset));
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Could not get typeface '" + asset + "': " + e.getMessage());
        }
        return false;
    }
}