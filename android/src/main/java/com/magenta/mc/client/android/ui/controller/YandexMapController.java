package com.magenta.mc.client.android.ui.controller;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

import com.google.gson.Gson;
import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.entity.AbstractStop;
import com.magenta.mc.client.android.entity.Address;
import com.magenta.mc.client.android.entity.LocationEntity;
import com.magenta.mc.client.android.handler.MapUpdateHandler;
import com.magenta.mc.client.android.mc.MxAndroidUtil;
import com.magenta.mc.client.android.service.storage.entity.Job;
import com.magenta.mc.client.android.ui.controller.yandex.BalloonClickListener;
import com.magenta.mc.client.android.ui.controller.yandex.RouteOverLay;
import com.magenta.mc.client.android.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.OverlayManager;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;

public class YandexMapController extends MapController {

    private ru.yandex.yandexmapkit.MapController controller;
    private OverlayManager overlayManager;
    private Overlay routeOverLay;
    private Overlay overlay;
    private Job job;

    public YandexMapController(final Activity activity, final List<AbstractStop> stops, final boolean routeWithDriver) {
        super(activity, stops, routeWithDriver);
        final View view = activity.getLayoutInflater().inflate(R.layout.view_yandex_map, null);
        MapView mapView = (MapView) view.findViewById(R.id.map);
        mapView.showScaleView(true);
        mHolder.removeAllViews();
        mHolder.addView(view);
        controller = mapView.getMapController();
        overlayManager = controller.getOverlayManager();
        overlayManager.getMyLocation().setEnabled(true);
        controller.setZoomCurrent(15);
        job = paintMap(stops);
        mHandler = new YandexHandler(this);
        mHandler.start();
        mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean isBuilding = true;

            public void onGlobalLayout() {
                if (isBuilding) {
                    isBuilding = false;
                } else {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    controller.showFindMeButton(false);
                    controller.showZoomButtons(false);
                    onMapReady();
                }
            }
        });
    }

    public void updateRoute(String route) {
        if (StringUtils.isBlank(route)) return;
        Double[][] coordinates = new Gson().fromJson(route, Double[][].class);
        if (routeOverLay != null) {
            overlayManager.removeOverlay(routeOverLay);
        }
        routeOverLay = new RouteOverLay(controller, coordinates);
        overlayManager.addOverlay(routeOverLay);
    }

    private Job paintMap(List<AbstractStop> stops) {
        if (overlay != null) {
            overlayManager.removeOverlay(overlay);
        }
        overlay = new Overlay(controller);
        Job job = null;
        for (final AbstractStop stop : stops) {
            if (job == null) {
                job = (Job) stop.getParentJob();
            }
            if (stop.getState() < 0) break;
            Address address = stop.getAddress();
            OverlayItem item = new OverlayItem(new GeoPoint(address.getLatitude(), address.getLongitude()), new BitmapDrawable(mActivity.getResources(), drawBitmap(stop.getPriority(), stop.getTimeAsString(), stop.isPickup())));
            BalloonItem balloon = new BalloonItem(mActivity, item.getGeoPoint());
            balloon.setText(stop.getAddressAsString());
            balloon.setOnBalloonListener(new BalloonClickListener() {
                public void onBalloonViewClick(BalloonItem balloonItem, View view) {
                    onJobTap(stop);
                }
            });
            item.setBalloonItem(balloon);
            overlay.addOverlayItem(item);
        }
        final Job finalJob = job;
        boolean start = false, end = false;
        if (job != null && job.getStartAddress() != null) {
            GeoPoint startAddress = new GeoPoint(job.getStartAddress().getLatitude(), job.getStartAddress().getLongitude());
            OverlayItem endItem = new OverlayItem(startAddress, mActivity.getResources().getDrawable(R.drawable.home_icon));
            BalloonItem startBalloon = new BalloonItem(mActivity, startAddress);
            startBalloon.setText(job.getStartAddress().getFullAddress());
            startBalloon.setOnBalloonListener(new BalloonClickListener() {
                public void onBalloonViewClick(BalloonItem balloonItem, View view) {
                    onStartTap(finalJob);
                }
            });
            endItem.setBalloonItem(startBalloon);
            overlay.addOverlayItem(endItem);
            start = true;
        }
        if (job != null && job.getEndAddress() != null) {
            GeoPoint endAddress = new GeoPoint(job.getEndAddress().getLatitude(), job.getEndAddress().getLongitude());
            OverlayItem endItem = new OverlayItem(endAddress, mActivity.getResources().getDrawable(R.drawable.home_icon));
            BalloonItem endBalloon = new BalloonItem(mActivity, endAddress);
            endBalloon.setText(job.getEndAddress().getFullAddress());
            endBalloon.setOnBalloonListener(new BalloonClickListener() {
                public void onBalloonViewClick(BalloonItem balloonItem, View view) {
                    onEndTap(finalJob);
                }
            });
            endItem.setBalloonItem(endBalloon);
            overlay.addOverlayItem(endItem);
            end = true;
        }
        if (job != null && (!start || !end)) {
            GeoPoint address = new GeoPoint(job.getAddress().getLatitude(), job.getAddress().getLongitude());
            controller.setPositionAnimationTo(address);
            OverlayItem item = new OverlayItem(address, mActivity.getResources().getDrawable(R.drawable.home_icon));
            BalloonItem balloon = new BalloonItem(mActivity, address);
            balloon.setText(job.getAddressAsString());
            balloon.setOnBalloonListener(new BalloonClickListener() {
                public void onBalloonViewClick(BalloonItem balloonItem, View view) {
                    onDCTap(finalJob);
                }
            });
            item.setBalloonItem(balloon);
            overlay.addOverlayItem(item);
        }
        overlayManager.addOverlay(overlay);
        return job;
    }

    public void fitBounds(List<Address> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            return;
        }
        if (addresses.size() == 1) {
            controller.setPositionNoAnimationTo(new GeoPoint(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()));
            controller.setZoomCurrent(18);
            return;
        }
        double maxLat, minLat, maxLon, minLon;
        maxLat = minLat = addresses.get(0).getLatitude();
        maxLon = minLon = addresses.get(0).getLongitude();
        for (Address address : addresses) {
            double lat = address.getLatitude();
            double lon = address.getLongitude();
            maxLat = Math.max(lat, maxLat);
            minLat = Math.min(lat, minLat);
            maxLon = Math.max(lon, maxLon);
            minLon = Math.min(lon, minLon);
        }
        controller.setPositionNoAnimationTo(new GeoPoint((maxLat + minLat) / 2, (maxLon + minLon) / 2));
        controller.setZoomToSpan(Math.abs(maxLat - minLat) * 1.1, Math.abs(maxLon - minLon) * 1.1);
    }

    protected void zoomIn() {
        super.zoomIn();
        controller.zoomIn();
    }

    protected void zoomOut() {
        super.zoomOut();
        controller.zoomOut();
    }

    protected void myLocation() {
        super.myLocation();
        if (controller.getOverlayManager().getMyLocation().getMyLocationItem() != null) {
            GeoPoint p = controller.getOverlayManager().getMyLocation().getMyLocationItem().getGeoPoint();
            if (p != null) {
                controller.setPositionAnimationTo(p, 15f);
            }
        }
    }

    private static class YandexHandler extends MapUpdateHandler {

        private final YandexMapController controller;

        public YandexHandler(YandexMapController controller) {
            this.controller = controller;
        }

        protected void updateMap(boolean firstRun) {
            LocationEntity location = MxAndroidUtil.getGeoLocation();
            if (location != null) {
                if (controller.mTrackCurrentPosition) {
                    controller.controller.setPositionAnimationTo(new GeoPoint(location.getLat(), location.getLon()));
                }
            }
            List<Address> addressList = new ArrayList<>();
            if (controller.routeWithDriver) {
                if (location != null) {
                    Address address = new Address();
                    address.setLatitude(location.getLat());
                    address.setLongitude(location.getLon());
                    addressList.add(address);
                }
                for (AbstractStop stop : controller.mStops) {
                    addressList.add(stop.getAddress());
                }
            } else if (firstRun) {
                addressList.add(controller.job.getStartAddress() != null ? controller.job.getStartAddress() : controller.job.getAddress());
                for (AbstractStop stop : controller.mStops) {
                    addressList.add(stop.getAddress());
                }
                addressList.add(controller.job.getEndAddress() != null ? controller.job.getEndAddress() : controller.job.getAddress());
            }
            controller.sendUpdateRequest(addressList);
        }
    }
}