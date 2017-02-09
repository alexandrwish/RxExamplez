package com.magenta.mc.client.android.ui.adapter;

public class NumericWheelAdapter implements WheelAdapter {

    private static final int DEFAULT_MAX_VALUE = 9;
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