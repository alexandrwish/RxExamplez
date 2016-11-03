package com.magenta.mc.client.android.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.ui.adapter.NumericWheelAdapter;
import com.magenta.mc.client.android.ui.view.WheelView;

import java.util.Calendar;

/**
 * Project: Santa-cruz
 * Author:  Alexandr Komarov
 * Created: 25.03.13 9:44
 * <p/>
 * Copyright (c) 1999-2013 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 * <p/>
 * $Id$
 */
public class WheelDialog extends WideDialog {

    protected WheelView firstWheel;
    protected WheelView secondWheel;

    protected int firstMin;
    protected int firstMax;
    protected int secondMin;
    protected int secondMax;

    protected WheelType type;

    protected NumericWheelAdapter firstWheelAdapter;
    protected NumericWheelAdapter secondWheelAdapter;

    protected View.OnClickListener okListener;

    public WheelDialog(Context context) {
        super(R.layout.dialog_time_input, context);
    }

    public WheelDialog(int layout, Context context, int theme) {
        super(layout, context, theme);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        beforeCreate();

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

    protected void beforeCreate() {
        //Todo initialize before create
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

    public enum WheelType {
        Time,
        Money,
        Break,
        Number,
        Other
    }
}
