package com.magenta.maxunits.mobile.dlib.controller.yandex;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import ru.yandex.yandexmapkit.overlay.IRender;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.utils.ScreenPoint;

public class RectRender implements IRender {

    @Override
    public void draw(Canvas canvas, OverlayItem item) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);
        OverlayRectItem rectItem = (OverlayRectItem) item;
        Path p = new Path();
        if (rectItem.getScreenPoints() != null && rectItem.getScreenPoints().size() > 0) {
            ScreenPoint screenPoint = rectItem.getScreenPoints().get(0);
            p.moveTo(screenPoint.getX(), screenPoint.getY());
            for (ScreenPoint point : rectItem.getScreenPoints()) {
                p.lineTo(point.getX(), point.getY());
            }
            canvas.drawPath(p, paint);
        }
    }
}