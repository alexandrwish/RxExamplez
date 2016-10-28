package com.magenta.rx.java.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.magenta.rx.R;
import com.magenta.rx.java.RXApplication;
import com.magenta.rx.java.event.ReceivedLocationEvent;
import com.magenta.rx.java.presenter.ServicePresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class ServiceActivity extends Activity implements OnMapReadyCallback {

    @Inject
    protected ServicePresenter presenter;

    private GoogleMap map;
    private LatLng prevPoint;
    private final DateFormat format = new SimpleDateFormat("yyyy.MMM.dd EEE HH:mm:ss", Locale.UK);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        ButterKnife.bind(this);
        RXApplication.getInstance().addServiceComponent(this);
        EventBus.getDefault().register(this);
        ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    protected void onDestroy() {
        RXApplication.getInstance().removeServiceComponent();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                presenter.load();   // TODO: 10/22/16 подумать, как менеджерить порядок загрузки ресурсов (>_<)
            }
        }, 5000);
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onReceivedLocation(ReceivedLocationEvent event) {
        LatLng newPoint = new LatLng(event.getLat(), event.getLon());
        map.addMarker(new MarkerOptions().position(newPoint).title(format.format(new Date(event.getTime()))));
        if (prevPoint != null) {
            map.addPolyline(new PolylineOptions().add(prevPoint).add(newPoint));
        }
        prevPoint = newPoint;
        EventBus.getDefault().removeStickyEvent(event);
    }
}