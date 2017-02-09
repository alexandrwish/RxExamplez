package com.magenta.mc.client.android.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.magenta.mc.client.log.MCLoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class SignatureView extends View {

    private static final float TOUCH_TOLERANCE = 4;
    protected List<Rect> lines = new LinkedList<>();
    private int width;
    private Paint mBitmapPaint;
    private Paint mPaint;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private int strokeWidth;
    private float mX, mY;

    public SignatureView(Context c) {
        this(c, 12);
    }

    public SignatureView(Context c, int strokeWidth) {
        super(c);
        this.strokeWidth = strokeWidth;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    protected void onDraw(Canvas canvas) {
        if (width != getWidth()) { // todo: check if that's what happens on screen rotation (not onDestroy()?)
            width = getWidth();
            clear();
        }
        canvas.drawColor(0xFFAAAAAA);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
    }

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        lines.add(new Rect((int) x, (int) y, -1, -1));
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            lines.add(new Rect((int) mX, (int) mY, (int) x, (int) y));
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
        //lines.add(new Rect((int) mX, (int) mY, 1, 1));
    }

    public boolean onTouchEvent(MotionEvent event) {
        String eventName = "";
        try {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    eventName = "ACTION_DOWN";
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    eventName = "ACTION_MOVE";
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    eventName = "ACTION_UP";
                    break;
            }
        } catch (Exception e) {
            MCLoggerFactory.getLogger(getClass()).error(String.format("Something wrong when user %s on SignatureVie", eventName), e);
        }
        return true;
    }

    public void clear() {
        free();
        mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        lines = new LinkedList<>();
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(this.strokeWidth);
        invalidate();
    }

    public String sign() {
        int np = lines.size();
        if (np == 0) {
            return "";
        }
        String temp = "";
        for (Rect p : lines) {
            temp = temp + p.left + ",";
            temp = temp + p.top + ",";
            temp = temp + p.right + ",";
            temp = temp + p.bottom + ",";
        }
        temp = temp.substring(0, temp.length() - 1);
        return temp;
    }

    public void free() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
            mCanvas = null;
            mPath = null;
            mBitmapPaint = null;
            mPaint = null;
            if (lines != null) {
                lines.clear();
                lines = null;
            }
        }
    }
}