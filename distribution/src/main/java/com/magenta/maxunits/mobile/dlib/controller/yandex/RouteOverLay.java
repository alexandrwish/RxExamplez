package com.magenta.maxunits.mobile.dlib.controller.yandex;

import com.magenta.maxunits.distribution.R;

import java.util.ArrayList;
import java.util.List;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;

public class RouteOverLay extends Overlay {

    protected OverlayRectItem overlayRectItem;
    protected MapController mMapController;

    public RouteOverLay(MapController mapController, Double[][] coordinates) {
        super(mapController);
        if (coordinates == null) return;
        mMapController = mapController;
        setIRender(new RectRender());
        overlayRectItem = new OverlayRectItem(new GeoPoint(0, 0), mapController.getContext().getResources().getDrawable(R.drawable.home_icon));
        List<GeoPoint> gp = new ArrayList<GeoPoint>();
        for (Double[] coordinate : coordinates) {
            gp.add(new GeoPoint(coordinate[0], coordinate[1]));
        }
        overlayRectItem.setGeoPoints(gp);
        addOverlayItem(overlayRectItem);
    }

    public List<OverlayItem> prepareDraw() {
        ArrayList<OverlayItem> draw = new ArrayList<OverlayItem>();
        overlayRectItem.getScreenPoints().clear();
        for (GeoPoint point : overlayRectItem.getGeoPoints()) {
            overlayRectItem.getScreenPoints().add(mMapController.getScreenPoint(point));
        }
        draw.add(overlayRectItem);
        return draw;
    }

    @Override
    public int compareTo(Object another) {
        return 0;
    }
}