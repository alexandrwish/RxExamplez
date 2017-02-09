package com.magenta.mc.client.android.ui.view;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

public class HtmlRef extends TextView {

    private String text;
    private String initialText;
    private OnTouchListener onClickListener;

    public HtmlRef(Context context) {
        super(context);
        init();
    }

    public HtmlRef(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HtmlRef(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setOnTouchListener(OnTouchListener l) {
        this.onClickListener = l;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        setRef(text);
    }

    public String getInitialText() {
        return initialText;
    }

    public void setInitialText() {
        this.text = initialText;
        setRef(initialText);
    }

    private void init() {
        initialText = super.getText().toString();
        this.text = initialText;
        setRef(initialText);
        setMovementMethod(new MovementMethod() {
            public void initialize(TextView widget, Spannable text) {
            }

            public boolean onKeyDown(TextView widget, Spannable text, int keyCode, KeyEvent event) {
                return false;
            }

            public boolean onKeyUp(TextView widget, Spannable text, int keyCode, KeyEvent event) {
                return false;
            }

            public boolean onKeyOther(TextView view, Spannable text, KeyEvent event) {
                return false;
            }

            public void onTakeFocus(TextView widget, Spannable text, int direction) {
            }

            public boolean onTrackballEvent(TextView widget, Spannable text, MotionEvent event) {
                return false;
            }

            public boolean onTouchEvent(TextView widget, Spannable text, MotionEvent event) {
                if (onClickListener != null) {
                    onClickListener.onTouch(HtmlRef.this, null);
                }
                return true;
            }

            public boolean onGenericMotionEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
                return false;
            }

            public boolean canSelectArbitrarily() {
                return false;
            }
        });
    }

    private void setRef(String text) {
        setText(Html.fromHtml(String.format("<a href=\"http://www.magenta-technology.com\">%s</a>", text)));
    }
}