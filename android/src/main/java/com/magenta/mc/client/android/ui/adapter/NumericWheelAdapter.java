package com.magenta.mc.client.android.ui.adapter;

/**
 * Project: Santa-cruz
 * Author:  Alexandr Komarov
 * Created: 25.03.13 9:33
 * <p/>
 * Copyright (c) 1999-2013 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 * <p/>
 * $Id$
 */

public class NumericWheelAdapter implements WheelAdapter {

    public static final int DEFAULT_MAX_VALUE = 9;
    private static final int DEFAULT_MIN_VALUE = 0;

    private int minValue;
    private int maxValue;

    /**
     * Default constructor
     */
    public NumericWheelAdapter() {
        this(DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
    }

    /**
     * Constructor
     *
     * @param minValue the wheel min value
     * @param maxValue the wheel maz value
     */
    public NumericWheelAdapter(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }


    public String getItem(int index) {
        index = Math.abs(index);
        if (index < getCount()) {
            return Integer.toString(minValue + index);
        } else if (index == getCount()) {
            return Integer.toString(minValue);
        }
        return null;
    }

    public int getCount() {
        return maxValue - minValue + 1;
    }

    public int getMaximumLength() {
        int max = Math.max(Math.abs(maxValue), Math.abs(minValue));
        int maxLen = Integer.toString(max).length();
        if (minValue < 0) {
            maxLen++;
        }
        return maxLen;
    }

    public void setMinBound(int minValue) {
        this.minValue = minValue;
    }

    public void setMaxBound(int maxValue) {
        this.maxValue = maxValue;
    }
}
