package com.magenta.maxunits.mobile.ui.controls;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

/**
 * @autor Petr Popov
 * Created 03.05.12 15:42
 */
public class SignatureView extends View {
    private static final float TOUCH_TOLERANCE = 4;
    private int width;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private List<Rect> lines = new LinkedList<Rect>();
    private Paint mPaint;
    private String savedSign;
    private float mX, mY;

    public SignatureView(Context c) {
        super(c);
    }

    public SignatureView(final Context c, final String savedSign) {
        super(c);
        this.savedSign = savedSign;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (width != getWidth()) {
            width = getWidth();
            lines = new LinkedList<Rect>();

            mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);

            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setColor(0xFFFF0000);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(12);

            if (savedSign != null) {
                buildPathFromSavedSign();
                mCanvas.drawPath(mPath, mPaint);
                mPath.reset();
                savedSign = null;
            }
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
            mX = x;
            mY = y;
            lines.add(new Rect((int) x, (int) y, 1, 1));
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
        lines.add(new Rect((int) mX, (int) mY, 1, 1));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    public void clear() {
        if (mCanvas != null) {
            mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            invalidate();
            lines.clear();
        }
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

    private void buildPathFromSavedSign() {
        lines.clear();
        final String[] parts = savedSign.split(",");
        int i = 0;
        int px = 0, py = 0;
        while (i + 4 <= parts.length) {
            final int x = Integer.parseInt(parts[i++]);
            final int y = Integer.parseInt(parts[i++]);
            final int w = Integer.parseInt(parts[i++]);
            final int h = Integer.parseInt(parts[i++]);
            if (w == -1 && h == -1) {
                mPath.moveTo(x, y);
                lines.add(new Rect(x, y, -1, -1));
            } else {
                mPath.quadTo(px, py, (x + px) / 2, (y + py) / 2);
                lines.add(new Rect(x, y, w, h));
            }
            px = x;
            py = y;
        }
    }
}
