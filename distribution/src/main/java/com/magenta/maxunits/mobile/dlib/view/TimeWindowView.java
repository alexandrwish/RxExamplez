package com.magenta.maxunits.mobile.dlib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.magenta.maxunits.distribution.R;

public class TimeWindowView extends LinearLayout {

    Context context;
    TimeView leftBound;
    TimeView rightBound;

    public TimeWindowView(Context context) {
        super(context);
        initView(context, null);
    }

    public TimeWindowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        this.context = context;
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_time_window, this);
        if (attrs == null) return;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimeView);
        leftBound = (TimeView) findViewById(R.id.left_bound);
        rightBound = (TimeView) findViewById(R.id.right_bound);
        setLeftBound(a.getString(R.styleable.TimeView_left_bound)).setParams(leftBound, a)
                .setRightBound(a.getString(R.styleable.TimeView_right_bound)).setParams(rightBound, a);
        a.recycle();
    }

    public TimeWindowView setLeftBound(String leftBound) {
        if (leftBound == null || leftBound.trim().isEmpty()) return this;
        String[] time = leftBound.replaceAll(" ", "").split("[:,-]");
        this.leftBound.setHours(time[0]).setMinutes(time[1]);
        return this;
    }

    public TimeWindowView setRightBound(String rightBound) {
        if (rightBound == null || rightBound.trim().isEmpty()) return this;
        String[] time = rightBound.replaceAll(" ", "").split("[:,-]");
        this.rightBound.setHours(time[0]).setMinutes(time[1]);
        return this;
    }

    private TimeWindowView setParams(TimeView view, TypedArray a) {
        view.setHoursColor(a.getColor(R.styleable.TimeView_hours_color, R.attr.defaultTextColor))
                .setHoursSize(a.getDimension(R.styleable.TimeView_hours_text_size, 12))
                .setMinutesColor(a.getColor(R.styleable.TimeView_minutes_color, R.attr.defaultTextColor))
                .setMinutesSize(a.getDimension(R.styleable.TimeView_minutes_text_size, 8));
        return this;
    }

    public TimeWindowView setVisibilityForDivider(int visibilityForDivider) {
        findViewById(R.id.left_bound_divider).setVisibility(visibilityForDivider);
        findViewById(R.id.right_bound_divider).setVisibility(visibilityForDivider);
        return this;
    }
}