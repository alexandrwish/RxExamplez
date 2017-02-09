package com.magenta.mc.client.android.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.ui.adapter.NumericWheelAdapter;
import com.magenta.mc.client.android.ui.view.WheelView;

import java.util.Calendar;

public class WheelDialog extends WideDialog {

    protected WheelType type;
    private WheelView firstWheel;
    private WheelView secondWheel;
    private int firstMin;
    private int firstMax;
    private int secondMin;
    private int secondMax;
    private NumericWheelAdapter firstWheelAdapter;
    private NumericWheelAdapter secondWheelAdapter;
    private View.OnClickListener okListener;

    public WheelDialog(Context context) {
        super(R.layout.dialog_time_input, context);
    }

    public WheelDialog(int layout, Context context, int theme) {
        super(layout, context, theme);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstWheelAdapter = new NumericWheelAdapter(firstMin, firstMax);
        firstWheel = (WheelView) findViewById(R.id.wheel_hour);
        firstWheel.setLabelBeforeValue(type.equals(WheelType.Money));
        firstWheel.setAdapter(firstWheelAdapter);
        secondWheelAdapter = new NumericWheelAdapter(secondMin, secondMax);
        secondWheel = (WheelView) findViewById(R.id.wheel_mins);
        secondWheel.setAdapter(secondWheelAdapter);
        Button okBtn = (Button) findViewById(R.id.wheel_ok_btn);
        okBtn.setOnClickListener(okListener);
        Button cancelBtn = (Button) findViewById(R.id.wheel_cancel_btn);
        cancelBtn.setOnClickListener(getCancelBtnListener());
        initialize();
    }

    protected void initialize() {
        // First initialize
        Calendar c = Calendar.getInstance();
        secondWheel.setCurrentItem(c.get(Calendar.MINUTE));
        firstWheel.setCurrentItem(c.get(Calendar.HOUR_OF_DAY));
    }

    public int getSecondValue() {
        return secondWheel.getCurrentItem();
    }

    public int getFirstValue() {
        return firstWheel.getCurrentItem();
    }

    public int getValue() {
        return 0;
    }

    public void setOkListener(View.OnClickListener okListener) {
        this.okListener = okListener;
    }

    //Override this method for cancel action
    protected View.OnClickListener getCancelBtnListener() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                cancel();
            }
        };
    }

    public void setValue(int firstValue, int secondValue) {
        firstWheel.setCurrentItem(firstValue);
        secondWheel.setCurrentItem(secondValue);
    }

    protected void setFirstWheelBound(int max, int min) {
        this.firstMax = max;
        this.firstMin = min;
    }

    protected void setSecondWheelBound(int max, int min) {
        this.secondMin = min;
        this.secondMax = max;
    }

    protected void setFirstWheelLabel(String label) {
        firstWheel.setLabel(label);
    }

    protected void setSecondWheelLabel(String label) {
        secondWheel.setLabel(label);
    }

    private enum WheelType {
        Time,
        Money,
        Break,
        Number,
        Other
    }
}