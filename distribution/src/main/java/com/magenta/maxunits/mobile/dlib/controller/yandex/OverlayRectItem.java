package com.magenta.maxunits.mobile.dlib.controller.yandex;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;
import ru.yandex.yandexmapkit.utils.ScreenPoint;

public class OverlayRectItem extends OverlayItem {

    private List<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
    private List<ScreenPoint> screenPoints = new ArrayList<ScreenPoint>();

    public OverlayRectItem(GeoPoint geoPoint, Drawable drawable) {
        super(geoPoint, drawable);
    }

    public List<GeoPoint> getGeoPoints() {
        return geoPoints;
    }

    public void setGeoPoints(List<GeoPoint> geoPoints) {
        this.geoPoints = geoPoints;
    }

    public List<ScreenPoint> getScreenPoints() {
        return screenPoints;
    }

    public void setScreenPoints(List<ScreenPoint> screenPoints) {
        this.screenPoints = screenPoints;
    }

    @Override
    public int compareTo(Object another) {
        return 0;
    }
}