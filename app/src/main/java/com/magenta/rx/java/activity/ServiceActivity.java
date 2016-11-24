package com.magenta.rx.java.activity;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.magenta.rx.java.R;
import com.magenta.rx.java.RXApplication;
import com.magenta.rx.java.presenter.ServicePresenter;
import com.magenta.rx.kotlin.event.ReceivedLocationEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class ServiceActivity extends Activity implements OnMapReadyCallback {

    @Inject
    ServicePresenter presenter;
    private GoogleMap map;
    private LatLng prevPoint;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        ButterKnife.bind(this);
        RXApplication.getInstance().getHolder().addServiceComponent(this);
        EventBus.getDefault().register(this);
        ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    protected void onDestroy() {
        RXApplication.getInstance().getHolder().removeServiceComponent();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onReceivedLocation(ReceivedLocationEvent event) {
        LatLng newPoint = new LatLng(event.getLat(), event.getLon());
        map.addMarker(new MarkerOptions().position(newPoint).title(new SimpleDateFormat("yyyy.MMM.dd EEE HH:mm:ss", Locale.UK).format(new Date(event.getTime()))));
        if (prevPoint != null) {
            map.addPolyline(new PolylineOptions().add(prevPoint).add(newPoint));
        }
        prevPoint = newPoint;
        EventBus.getDefault().removeStickyEvent(event);
    }
}