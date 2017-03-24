package com.magenta.mc.client.android.ui.controller;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.entity.AbstractStop;
import com.magenta.mc.client.android.entity.Address;
import com.magenta.mc.client.android.entity.Job;
import com.magenta.mc.client.android.entity.LocationEntity;
import com.magenta.mc.client.android.handler.MapUpdateHandler;
import com.magenta.mc.client.android.log.MCLoggerFactory;
import com.magenta.mc.client.android.service.ServicesRegistry;
import com.magenta.mc.client.android.util.MxAndroidUtil;
import com.magenta.mc.client.android.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class GoogleMapController extends MapController implements OnMapReadyCallback {

    private GoogleMap map;
    private MarkerOptions currentLocation;
    private boolean traffic;
    private Job job;

    public GoogleMapController(Activity activity, final List<AbstractStop> stops, final boolean routeWithDriver) {
        super(activity, stops, routeWithDriver);
        final View view = activity.getLayoutInflater().inflate(R.layout.view_google_map, null);
        mHolder.addView(view);
        ((MapFragment) activity.getFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            protected boolean isBuilding = true;

            public void onGlobalLayout() {
                if (isBuilding) {
                    isBuilding = false;
                } else {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    onMapReady();
                }
            }
        });
    }

    protected Job paintMap(List<AbstractStop> stops) {
        map.clear();
        Job job = null;
        for (AbstractStop stop : stops) {
            if (job == null) {
                job = (Job) stop.getParentJob();
            }
            if (stop.getState() < 0) break;
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(stop.getAddress().getLatitude(), stop.getAddress().getLongitude()))
                    .icon(BitmapDescriptorFactory.fromBitmap(drawBitmap(stop.getPriority(), stop.getTimeAsString(), stop.isPickup())))
                    .snippet(stop.getReferenceId()));
        }
        if (job != null && job.getAddress() != null) {
            LatLng centerCoordinates = new LatLng(job.getAddress().getLatitude(), job.getAddress().getLongitude());
            CameraUpdate center = CameraUpdateFactory.newLatLng(centerCoordinates);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            boolean start = false, end = false;
            map.moveCamera(center);
            map.animateCamera(zoom);
            if (job.getStartAddress() != null) {
                LatLng startCoordinates = new LatLng(job.getStartAddress().getLatitude(), job.getStartAddress().getLongitude());
                map.addMarker(new MarkerOptions()
                        .position(startCoordinates)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.home_icon))
                        .snippet("Start"));
                start = true;
            }
            if (job.getEndAddress() != null) {
                LatLng endCoordinates = new LatLng(job.getEndAddress().getLatitude(), job.getEndAddress().getLongitude());
                map.addMarker(new MarkerOptions()
                        .position(endCoordinates)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.home_icon))
                        .snippet("End"));
                end = true;
            }
            if (!start || !end) {
                map.addMarker(new MarkerOptions()
                        .position(centerCoordinates)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.home_icon))
                        .snippet("DC"));
            }
        }
        return job;
    }

    public void updateRoute(String route) {
        if (StringUtils.isBlank(route)) return;
        Double[][] coordinates = new Gson().fromJson(route, Double[][].class);
        LatLng[] points = new LatLng[coordinates.length];
        for (int i = 0; i < coordinates.length; i++) {
            points[i] = new LatLng(coordinates[i][0], coordinates[i][1]);
        }
        map.addPolyline(new PolylineOptions().add(points).color(Color.BLACK));
    }

    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            job = paintMap(mStops);
            map.getUiSettings().setZoomControlsEnabled(false);
            LocationEntity location = MxAndroidUtil.getGeoLocation();
            if (location != null) {
                currentLocation = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.truck_icon))
                        .position(new LatLng(location.getLat(), location.getLon()))
                        .snippet("CL");
                map.addMarker(currentLocation);
            }
            map.getUiSettings().setMyLocationButtonEnabled(true);
            mActivity.findViewById(R.id.traffic).setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    traffic = !traffic;
                    map.setTrafficEnabled(traffic);
                }
            });
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                public boolean onMarkerClick(Marker marker) {
                    if (marker.getSnippet().equalsIgnoreCase("DC")) {
                        onDCTap(job);
                    } else if (marker.getSnippet().equalsIgnoreCase("CL")) {
                        MCLoggerFactory.getLogger(getClass()).info("onCLTab");
                    } else if (marker.getSnippet().equalsIgnoreCase("End")) {
                        onEndTap(job);
                    } else if (marker.getSnippet().equalsIgnoreCase("Start")) {
                        onStartTap(job);
                    } else {
                        AbstractStop stop = null;
                        for (AbstractStop s : mStops) {
                            if (s.getReferenceId().equalsIgnoreCase(marker.getSnippet())) {
                                stop = s;
                            }
                        }
                        if (stop == null) {
                            return false;
                        }
                        onJobTap(stop);
                    }
                    return true;
                }
            });
            mHandler = new GoogleHandler(this);
            mHandler.start();
        }
    }

    public void fitBounds(List<Address> addresses) {
        if (addresses.size() == 0) {
            return;
        }
        if (addresses.size() == 1) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()), 16f));
            return;
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Address address : addresses) {
            builder.include(new LatLng(address.getLatitude(), address.getLongitude()));
        }
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 70));
    }

    protected void zoomIn() {
        super.zoomIn();
        map.animateCamera(CameraUpdateFactory.zoomTo(map.getCameraPosition().zoom + 1f));
    }

    protected void zoomOut() {
        super.zoomOut();
        map.animateCamera(CameraUpdateFactory.zoomTo(map.getCameraPosition().zoom - 1f));
    }

    protected void myLocation() {
        super.myLocation();
        Location loc = ServicesRegistry.getLocationService().getLocation();
        if (loc != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 16f));
        }
    }

    private static class GoogleHandler extends MapUpdateHandler {

        final GoogleMapController controller;

        GoogleHandler(GoogleMapController controller) {
            this.controller = controller;
        }

        protected void updateMap(boolean firstRun) {
            List<Address> addressList = new ArrayList<Address>();
            LocationEntity location = MxAndroidUtil.getGeoLocation();
            if (location != null) {
                LatLng centerCoordinates = new LatLng(location.getLat(), location.getLon());
                if (controller.mTrackCurrentPosition) {
                    controller.map.animateCamera(CameraUpdateFactory.newLatLng(centerCoordinates));
                }
                if (controller.currentLocation == null) {
                    controller.currentLocation = new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.truck_icon))
                            .position(centerCoordinates)
                            .snippet("CL");
                    controller.map.addMarker(controller.currentLocation);
                } else {
                    controller.currentLocation.position(centerCoordinates);
                }
            }
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