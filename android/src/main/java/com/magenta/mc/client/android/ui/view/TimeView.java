package com.magenta.mc.client.android.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.util.DisplayUtils;

public class TimeView extends LinearLayout {

    Context context;
    TextView hoursView;
    TextView minutesView;

    public TimeView(Context context) {
        super(context);
        initView(context, null);
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        this.context = context;
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_time, this);
        hoursView = (TextView) findViewById(R.id.hours);
        minutesView = (TextView) findViewById(R.id.minutes);
        if (attrs == null) return;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimeView);
        setHours(a.getInteger(R.styleable.TimeView_hours, 0))
                .setHoursColor(a.getColor(R.styleable.TimeView_hours_color, R.attr.defaultTextColor))
                .setHoursSize(a.getDimension(R.styleable.TimeView_hours_text_size, 12))
                .setMinutes(a.getInteger(R.styleable.TimeView_minutes, 0))
                .setMinutesColor(a.getColor(R.styleable.TimeView_minutes_color, R.attr.defaultTextColor))
                .setMinutesSize(a.getDimension(R.styleable.TimeView_minutes_text_size, 8));
        a.recycle();
    }

    public TimeView setHours(Integer hours) {
        String hrStr = hours.toString();
        if (hrStr.length() == 1) {
            hrStr = "0" + hrStr;
        }
        this.hoursView.setText(hrStr);
        return this;
    }

    public TimeView setMinutes(Integer minutes) {
        String minStr = minutes.toString();
        if (minStr.length() == 1) {
            minStr = "0" + minStr;
        }
        this.minutesView.setText(minStr);
        return this;
    }

    public TimeView setHours(String hours) {
        this.hoursView.setText(hours);
        return this;
    }

    public TimeView setMinutes(String minutes) {
        this.minutesView.setText(minutes);
        return this;
    }

    public TimeView setHoursColor(int color) {
        this.hoursView.setTextColor(color);
        return this;
    }

    public TimeView setMinutesColor(int color) {
        this.minutesView.setTextColor(color);
        return this;
    }

    public TimeView setHoursSize(float dimension) {
        this.hoursView.setTextSize(dimension / DisplayUtils.density(context));
        return this;
    }

    public TimeView setMinutesSize(float dimension) {
        this.minutesView.setTextSize(dimension / DisplayUtils.density(context));
        return this;
    }

    public TimeView setTime(String time) {
        if (time == null || time.trim().isEmpty()) return this;
        String[] times = time.replaceAll(" ", "").split("[:,-]");
        return setHours(times[0]).setMinutes(times[1]);
    }
}