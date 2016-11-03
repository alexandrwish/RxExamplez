package com.magenta.mc.client.android.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.ui.adapter.NumericWheelAdapter;
import com.magenta.mc.client.android.ui.adapter.WheelAdapter;

/**
 * Project: Santa-cruz
 * Author:  Alexandr Komarov
 * Created: 25.03.13 9:29
 * <p/>
 * Copyright (c) 1999-2013 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 * <p/>
 * $Id$
 */
public class WheelView extends View {

    /**
     * Current value & label text color
     */
    private static final int VALUE_TEXT_COLOR = 0xE0000000;

    /**
     * Items text color
     */
    private static final int ITEMS_TEXT_COLOR = 0xFF000000;

    /**
     * Top and bottom shadows colors
     */
    private static final int[] SHADOWS_COLORS = new int[]{0xFF111111, 0x00AAAAAA, 0x00AAAAAA};

    /**
     * Additional items height (is added to standard text item height)
     */
    private static final int ADDITIONAL_ITEM_HEIGHT = 15;

    /**
     * Text size
     */
    private static final int TEXT_SIZE = 24;

    /**
     * Top and bottom items offset (to hide that)
     */
    private static final int ITEM_OFFSET = TEXT_SIZE / 5;

    /**
     * Additional width for items layout
     */
    private static final int ADDITIONAL_ITEMS_SPACE = 10;

    /**
     * Label offset
     */
    private static final int LABEL_OFFSET = 8;

    /**
     * Left and right padding value
     */
    private static final int PADDING = 10;

    /**
     * Default count of visible items
     */
    private static final int DEF_VISIBLE_ITEMS = 5;
    // 0 - time;
    // 1, 2 - y coordinates;
    private final float[] onTouchParams = {0, 0, 0};
    // Wheel Values
    private WheelAdapter adapter = new NumericWheelAdapter();
    private int currentItem = 0;
    // Widths
    private int itemsWidth = 0;
    private int labelWidth = 0;
    // Count of visible items
    private int visibleItems = DEF_VISIBLE_ITEMS;
    // Text paints
    private TextPaint itemsPaint;
    private TextPaint valuePaint;
    // Layouts
    private StaticLayout itemsLayout;
    private StaticLayout labelLayout;
    private StaticLayout valueLayout;
    // Label & background
    private String label;
    private Drawable centerDrawable;
    // Shadows drawables
    private GradientDrawable topShadow;
    private GradientDrawable bottomShadow;
    // Last touch Y position
    private float lastYTouch;
    private boolean labelBeforeValue = false;
    //Handlers for auto scrolling
    private Handler touchTimer = new Handler();
    final private Runnable timerRunnable = new Runnable() {
        public void run() {
            onTouchParams[0] += 1;

            touchTimer.removeCallbacks(this);
            touchTimer.postDelayed(this, 100);
        }
    };
    private Handler afterTouchHandler = new Handler();
    private Handler touchHandler = new Handler(Looper.getMainLooper());
    private Runnable actionRunnable;

    /**
     * Constructor
     */
    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Constructor
     */
    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructor
     */
    public WheelView(Context context) {
        super(context);
    }

    /**
     * Gets wheel adapter
     *
     * @return the adapter
     */
    public WheelAdapter getAdapter() {
        return adapter;
    }

    /**
     * Sets wheel adapter
     *
     * @param adapter the new wheel adapter
     */
    public void setAdapter(WheelAdapter adapter) {
        this.adapter = adapter;
        invalidate();
    }

    /**
     * Gets count of visible items
     *
     * @return the count of visible items
     */
    public int getVisibleItems() {
        return visibleItems;
    }

    /**
     * Sets count of visible items
     *
     * @param count the new count
     */
    public void setVisibleItems(int count) {
        visibleItems = count;
        invalidate();
    }

    /**
     * Gets label
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets label
     *
     * @param newLabel the label to set
     */
    public void setLabel(String newLabel) {
        label = newLabel;
        labelLayout = null;
        invalidate();
    }

    /**
     * Gets current value
     *
     * @return the current value
     */
    public int getCurrentItem() {
        return currentItem;
    }

    /**
     * Sets the current item
     *
     * @param index the item index
     */
    public void setCurrentItem(int index) {
        if (index != currentItem) {
            itemsLayout = null;
            valueLayout = null;
            currentItem = index;
            invalidate();
        }
    }

    /**
     * Initializes resources
     */
    private void initResourcesIfNecessary() {
        if (itemsPaint == null) {
            itemsPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
            itemsPaint.density = getResources().getDisplayMetrics().density;
            itemsPaint.setTextSize(TEXT_SIZE);
        }

        if (valuePaint == null) {
            valuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG | Paint.DITHER_FLAG);
            valuePaint.density = getResources().getDisplayMetrics().density;
            valuePaint.setTextSize(TEXT_SIZE);
            valuePaint.setShadowLayer(0.5f, 0, 0.5f, 0xFFFFFFFF);
        }

        if (centerDrawable == null) {
            centerDrawable = getContext().getResources().getDrawable(R.drawable.wheel_val);
        }

        if (topShadow == null) {
            topShadow = new GradientDrawable(Orientation.TOP_BOTTOM, SHADOWS_COLORS);
        }

        if (bottomShadow == null) {
            bottomShadow = new GradientDrawable(Orientation.BOTTOM_TOP, SHADOWS_COLORS);
        }

        setBackgroundResource(R.drawable.wheel_bg);
    }

    /**
     * Calculates desired height for layout
     *
     * @param layout the source layout
     * @return the desired layout height
     */
    private int getDesiredHeight(Layout layout) {
        if (layout == null) {
            return 0;
        }

        int linecount = layout.getLineCount();
        int desired = layout.getLineTop(linecount) - ITEM_OFFSET * 2 - ADDITIONAL_ITEM_HEIGHT;

        // Check against our minimum height
        desired = Math.max(desired, getSuggestedMinimumHeight());

        return desired;
    }

    /**
     * Builds text depending on current value
     *
     * @return the text
     */
    private String buildText() {
        WheelAdapter adapter = getAdapter();
        StringBuilder itemsText = new StringBuilder();
        int addItems = visibleItems / 2;
        for (int i = currentItem - addItems; i < currentItem; i++) {
            String text;
            if (i >= 0 && adapter != null) {
                text = adapter.getItem(i);
            } else {
                text = adapter.getItem(adapter.getCount() + i);
            }
            if (text != null) {
                itemsText.append(text);
            }
            itemsText.append("\n");
        }

        itemsText.append("\n"); // here will be current value

        for (int i = currentItem + 1; i <= currentItem + addItems; i++) {
            String text;
            if (i < adapter.getCount()) {
                text = adapter.getItem(i);
            } else {
                text = adapter.getItem(i - adapter.getCount());
            }
            if (text != null) {
                itemsText.append(text);
            }
            if (i < currentItem + addItems) {
                itemsText.append("\n");
            }
        }
        return itemsText.toString();
    }

    /**
     * Returns the max item length that can be present
     *
     * @return the max length
     */
    private int getMaxTextLength() {
        WheelAdapter adapter = getAdapter();
        if (adapter == null) {
            return 0;
        }

        int adapterLength = adapter.getMaximumLength();
        if (adapterLength > 0) {
            return adapterLength;
        }

        String maxText = null;
        int addItems = visibleItems / 2;
        for (int i = Math.max(currentItem - addItems, 0);
             i < Math.min(currentItem + visibleItems, adapter.getCount()); i++) {
            String text = adapter.getItem(i);
            if (text != null && (maxText == null || maxText.length() < text.length())) {
                maxText = text;
            }
        }

        return maxText != null ? maxText.length() : 0;
    }

    /**
     * Calculates control width and creates text layouts
     *
     * @param widthSize the input layout width
     * @param mode      the layout mode
     * @return the calculated control width
     */
    private int calculateLayoutWidth(int widthSize, int mode) {
        initResourcesIfNecessary();

        int width = widthSize;

        int maxLength = getMaxTextLength();
        if (maxLength > 0) {
            double textWidth = Math.ceil(Layout.getDesiredWidth("0", itemsPaint));
            itemsWidth = (int) (maxLength * textWidth);
        } else {
            itemsWidth = 0;
        }
        itemsWidth += ADDITIONAL_ITEMS_SPACE; // make it some more

        labelWidth = 0;
        if (label != null && label.length() > 0) {
            labelWidth = (int) Math.ceil(Layout.getDesiredWidth(label, valuePaint));
        }

        boolean recalculate = false;
        if (mode == MeasureSpec.EXACTLY) {
            width = widthSize;
            recalculate = true;
        } else {
            width = itemsWidth + labelWidth + 2 * PADDING;
            if (labelWidth > 0) {
                width += LABEL_OFFSET;
            }

            // Check against our minimum width
            width = Math.max(width, getSuggestedMinimumWidth());

            if (mode == MeasureSpec.AT_MOST && widthSize < width) {
                width = widthSize;
                recalculate = true;
            }
        }

        if (recalculate) {
            // recalculate width
            int pureWidth = width - LABEL_OFFSET - 2 * PADDING;
            if (pureWidth <= 0) {
                itemsWidth = labelWidth = 0;
            }
            if (labelWidth > 0) {
                double newWidthItems = (double) itemsWidth * pureWidth / (itemsWidth + labelWidth);
                itemsWidth = (int) newWidthItems;
                labelWidth = pureWidth - itemsWidth;
            } else {
                itemsWidth = pureWidth + LABEL_OFFSET; // no label
            }
        }

        if (itemsWidth > 0) {
            createLayouts(itemsWidth, labelWidth);
        }

        return width;
    }

    /**
     * Creates layouts
     *
     * @param widthItems width of items layout
     * @param widthLabel width of label layout
     */
    private void createLayouts(int widthItems, int widthLabel) {
        if (itemsLayout == null || itemsLayout.getWidth() > widthItems) {
            itemsLayout = new StaticLayout(
                    buildText(),
                    itemsPaint,
                    widthItems,
                    widthLabel > 0
                            ? Layout.Alignment.ALIGN_OPPOSITE
                            : Layout.Alignment.ALIGN_CENTER,
                    1,
                    ADDITIONAL_ITEM_HEIGHT,
                    false);
        } else {
            itemsLayout.increaseWidthTo(widthItems);
        }

        if (valueLayout == null || valueLayout.getWidth() > widthItems) {
            String text = getAdapter() != null ? getAdapter().getItem(currentItem) : null;
            valueLayout = new StaticLayout(
                    text != null ? text : "",
                    valuePaint,
                    widthItems,
                    widthLabel > 0
                            ? Layout.Alignment.ALIGN_OPPOSITE
                            : Layout.Alignment.ALIGN_CENTER,
                    1,
                    ADDITIONAL_ITEM_HEIGHT,
                    false);
        } else {
            valueLayout.increaseWidthTo(widthItems);
        }

        if (widthLabel > 0) {
            if (labelLayout == null || labelLayout.getWidth() > widthLabel) {
                labelLayout = new StaticLayout(
                        label,
                        valuePaint,
                        widthLabel,
                        Layout.Alignment.ALIGN_NORMAL,
                        1,
                        ADDITIONAL_ITEM_HEIGHT,
                        false);
            } else {
                labelLayout.increaseWidthTo(widthLabel);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = calculateLayoutWidth(widthSize, widthMode);

        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = getDesiredHeight(itemsLayout);

            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (itemsLayout == null) {
            if (itemsWidth == 0) {
                calculateLayoutWidth(getWidth(), MeasureSpec.EXACTLY);
            } else {
                createLayouts(itemsWidth, labelWidth);
            }
        }

        drawCenterRect(canvas);

        if (itemsWidth > 0) {
            canvas.save();
            // Skip padding space and hide a part of top and bottom items
            canvas.translate(PADDING, -ITEM_OFFSET);
            drawItems(canvas);
            drawValue(canvas);
            canvas.restore();
        }

        drawShadows(canvas);
    }

    /**
     * Draws shadows on top and bottom of control
     *
     * @param canvas the canvas for drawing
     */
    private void drawShadows(Canvas canvas) {
        topShadow.setBounds(0, 0, getWidth(), getHeight() / visibleItems);
        topShadow.draw(canvas);

        bottomShadow.setBounds(0, getHeight() - getHeight() / visibleItems,
                getWidth(), getHeight());
        bottomShadow.draw(canvas);
    }

    /**
     * Draws value and label layout
     *
     * @param canvas the canvas for drawing
     */
    private void drawValue(Canvas canvas) {
        valuePaint.setColor(VALUE_TEXT_COLOR);
        valuePaint.drawableState = getDrawableState();

        Rect bounds = new Rect();
        itemsLayout.getLineBounds(visibleItems / 2, bounds);

        // draw label
        if (labelLayout != null) {
            canvas.save();
            canvas.translate((labelBeforeValue ? LABEL_OFFSET * 2 : itemsLayout.getWidth()) + LABEL_OFFSET, bounds.top);
            labelLayout.draw(canvas);
            canvas.restore();
        }

        // draw current value
        canvas.save();
        canvas.translate(0, bounds.top);
        valueLayout.draw(canvas);
        canvas.restore();
    }

    /**
     * Draws items
     *
     * @param canvas the canvas for drawing
     */
    private void drawItems(Canvas canvas) {
        itemsPaint.setColor(ITEMS_TEXT_COLOR);
        itemsPaint.drawableState = getDrawableState();
        itemsLayout.draw(canvas);
    }

    /**
     * Draws rect for current value
     *
     * @param canvas the canvas for drawing
     */
    private void drawCenterRect(Canvas canvas) {
        int center = getHeight() / 2;
        int offset = getHeight() / visibleItems / 2;
        centerDrawable.setBounds(0, center - offset, getWidth(), center + offset);
        centerDrawable.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        WheelAdapter adapter = getAdapter();
        if (adapter == null) {
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastYTouch = event.getY();
                onTouchParams[0] = 1;
                onTouchParams[1] = event.getY();
                touchTimer.postDelayed(timerRunnable, 100);
                if (actionRunnable != null) {
                    touchHandler.removeCallbacks(actionRunnable);
                }

                break;

            case MotionEvent.ACTION_MOVE:
                float delta = event.getY() - lastYTouch;
                int count = (int) (visibleItems * delta / getHeight());
                if (action(count)) {
                    lastYTouch = event.getY();
                }
                break;
            case MotionEvent.ACTION_UP:
                onTouchParams[2] = event.getY();
                int counts = (int) ((visibleItems * (onTouchParams[2] - onTouchParams[1])) / getHeight());
                final boolean[] increment = {counts > 0};
                counts = Math.abs(counts);
                final long delay = (long) onTouchParams[0];

                if (counts > 1 && delay < 10) {
                    final float countsPerTime = delay * 100 / counts;

                    touchTimer.removeCallbacks(timerRunnable);

                    actionRunnable = new Runnable() {
                        public void run() {
                            action(!increment[0] ? -1 : 1);

                            touchHandler.removeCallbacks(this);
                            touchHandler.postDelayed(this, (long) countsPerTime);
                        }
                    };

                    touchHandler.postDelayed(actionRunnable, (long) countsPerTime);

                    afterTouchHandler.postDelayed(new Runnable() {
                        public void run() {
                            touchHandler.removeCallbacks(actionRunnable);
                        }
                    }, counts * delay * 150);
                }
                break;
        }
        return true;
    }

    private boolean action(int count) {
        int pos = currentItem - count;
//                pos = Math.max(pos, 0);
//                pos = Math.min(pos, adapter.getCount());
        if (pos < 0) {
            pos = adapter.getCount() + pos;
        } else if (pos >= adapter.getCount()) {
            pos = pos - adapter.getCount();
        }
        if (pos != currentItem) {
            setCurrentItem(pos);
            return true;
        }
        return false;
    }

    public void setLabelBeforeValue(boolean beforeValue) {
        this.labelBeforeValue = beforeValue;
    }
}
