package com.magenta.mc.client.android.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magenta.mc.client.android.R;

import java.util.Locale;

public class NumberInput extends LinearLayout {

    protected final int MIN_DELAY_INTERVAL = 60;
    protected final int MAX_DELAY_INTERVAL = 500;
    protected int delayInterval = MAX_DELAY_INTERVAL;
    private FrameLayout integerPartContainer;
    private FrameLayout fractionPartContainer;
    private TextView integerView;
    private TextView fractionView;
    private long integerPart;
    private double fractionPart;
    private Handler handler;
    private Runnable runnableIntegerPart;
    private Runnable runnableFractionPart;
    private Handler incrementSpeedHandler;
    private IncrementalRunnable incrementSpeedRunnable;
    private boolean plusDown;
    private boolean minusDown;

    public NumberInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context);
    }

    public NumberInput(Context context) {
        super(context);
        inflate(context);
    }

    private Runnable getRunnable(final boolean isFractionPart) {
        if (isFractionPart) {
            if (runnableFractionPart == null) {
                runnableFractionPart = new Runnable() {
                    public void run() {
                        if (plusDown) {
                            incrementPartForListener(true);
                            handler.postDelayed(this, delayInterval);
                        }
                        if (minusDown) {
                            decrementPartForListener(true);
                            handler.postDelayed(this, delayInterval);
                        }
                    }
                };
            }
            return runnableFractionPart;
        } else {
            if (runnableIntegerPart == null) {
                runnableIntegerPart = new Runnable() {
                    public void run() {
                        if (plusDown) {
                            incrementPartForListener(false);
                            handler.postDelayed(this, delayInterval);
                        }
                        if (minusDown) {
                            decrementPartForListener(false);
                            handler.postDelayed(this, delayInterval);
                        }
                    }
                };
            }
            return runnableIntegerPart;
        }
    }

    private void inflate(Context context) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.part_number_input_container, this);
        fractionPartContainer = (FrameLayout) findViewById(R.id.fraction_part);
        integerPartContainer = (FrameLayout) findViewById(R.id.integer_part);
        integerView = (TextView) integerPartContainer.findViewById(R.id.number_field);
        fractionView = (TextView) fractionPartContainer.findViewById(R.id.number_field);
        handler = new Handler();
        incrementSpeedHandler = new Handler();
        incrementSpeedRunnable = new IncrementalRunnable() {
            public void run() {
                if (runnable == null) return;
                if (delayInterval > MIN_DELAY_INTERVAL) {
                    delayInterval -= 50;
                    handler.postDelayed(runnable, delayInterval);
                }
                incrementSpeedHandler.postDelayed(this, 1000);
            }
        };
        setControlHandlers(integerPartContainer, false);
        setControlHandlers(fractionPartContainer, true);
    }

    public void initWithNumber(long number) {
        fractionPartContainer.setVisibility(View.GONE);
        integerView.setText("" + number);
        fractionPart = 0;
        fractionView.setText(".00");
    }

    public void initWithNumber(double number) {
        fractionPartContainer.setVisibility(View.VISIBLE);
        integerPart = (long) number;
        fractionPart = number - integerPart;
        integerView.setText("" + integerPart);
        fractionView.setText(String.format(Locale.UK, "%1.2f", fractionPart).substring(1));
    }

    public double getDoubleNumber() {
        return integerPart + fractionPart;
    }

    public long getIntegerNumber() {
        return integerPart;
    }

    private void incrementPartForListener(final boolean isFractionPart) {
        integerPart = Integer.parseInt(integerView.getText().toString().replace(".", ""));
        if (isFractionPart) {
            fractionPart = Double.parseDouble("0" + fractionView.getText().toString());
            if (fractionPart < 0.99) {
                fractionPart += 0.01;
            } else {
                if (integerPart < 999) {
                    fractionPart = 0.00;
                    integerPart++;
                }
            }
            integerView.setText("" + integerPart);
            fractionView.setText(String.format(Locale.UK, "%1.2f", fractionPart).substring(1));
        } else {
            if (integerPart < 999) {
                integerView.setText("" + ++integerPart);
            }
        }
    }

    private void decrementPartForListener(final boolean isFractionPart) {
        integerPart = Integer.parseInt(integerView.getText().toString().replace(".", ""));
        if (isFractionPart) {
            fractionPart = Double.parseDouble("0" + fractionView.getText().toString());
            fractionPart -= 0.01;
            if (fractionPart < 0.00) {
                fractionPart = 0.99;
                integerPart--;
                if (integerPart < 0) {
                    integerPart = 0;
                    fractionPart = 0.00;
                }
            }
            integerView.setText("" + integerPart);
            fractionView.setText(String.format(Locale.UK, "%1.2f", fractionPart).substring(1));
        } else if (integerPart > 0) {
            integerView.setText("" + --integerPart);
        }
    }

    private void setControlHandlers(ViewGroup container, final boolean isFractionPart) {
        Button plus = (Button) container.findViewById(R.id.plus_button);
        Button minus = (Button) container.findViewById(R.id.minus_button);
        plus.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                incrementPartForListener(isFractionPart);
            }
        });
        plus.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case (MotionEvent.ACTION_UP): {
                        plusDown = false;
                        handler.removeCallbacks(getRunnable(isFractionPart));
                        incrementSpeedHandler.removeCallbacks(incrementSpeedRunnable.clear());
                        delayInterval = MAX_DELAY_INTERVAL;
                        break;
                    }
                    case (MotionEvent.ACTION_DOWN): {
                        plusDown = true;
                        final Runnable runnable = getRunnable(isFractionPart);
                        handler.post(runnable);
                        incrementSpeedHandler.post(incrementSpeedRunnable.setInternalRunnable(runnable));
                        break;
                    }
                    case (MotionEvent.ACTION_MOVE): {
                        int x = Math.round(event.getX());
                        int y = Math.round(event.getY());
                        Rect rect = new Rect();
                        v.getLocalVisibleRect(rect);
                        if (rect.contains(x, y)) {
                            break;
                        } else {
                            plusDown = false;
                            handler.removeCallbacks(getRunnable(isFractionPart));
                            incrementSpeedHandler.removeCallbacks(incrementSpeedRunnable.clear());
                            delayInterval = MAX_DELAY_INTERVAL;
                        }
                        break;
                    }
                }
                return true;
            }
        });
        minus.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                decrementPartForListener(isFractionPart);
            }
        });
        minus.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case (MotionEvent.ACTION_UP): {
                        minusDown = false;
                        handler.removeCallbacks(getRunnable(isFractionPart));
                        incrementSpeedHandler.removeCallbacks(incrementSpeedRunnable.clear());
                        delayInterval = MAX_DELAY_INTERVAL;
                        break;
                    }
                    case (MotionEvent.ACTION_DOWN): {
                        minusDown = true;
                        final Runnable runnable = getRunnable(isFractionPart);
                        handler.post(runnable);
                        incrementSpeedHandler.post(incrementSpeedRunnable.setInternalRunnable(runnable));
                        break;
                    }
                    case (MotionEvent.ACTION_MOVE): {
                        int x = Math.round(event.getX());
                        int y = Math.round(event.getY());
                        Rect rect = new Rect();
                        v.getLocalVisibleRect(rect);
                        if (rect.contains(x, y)) {
                            break;
                        } else {
                            minusDown = false;
                            handler.removeCallbacks(getRunnable(isFractionPart));
                            incrementSpeedHandler.removeCallbacks(incrementSpeedRunnable.clear());
                            delayInterval = MAX_DELAY_INTERVAL;
                        }
                    }
                }
                return true;
            }
        });
    }

    private abstract class IncrementalRunnable implements Runnable {

        protected Runnable runnable;

        public Runnable setInternalRunnable(Runnable runnable) {
            this.runnable = runnable;
            return this;
        }

        public Runnable clear() {
            this.runnable = null;
            return this;
        }
    }
}