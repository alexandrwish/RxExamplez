package com.magenta.rx.java.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.magenta.rx.java.R;
import com.magenta.rx.java.RXApplication;
import com.magenta.rx.java.event.CleanMapEvent;
import com.magenta.rx.java.event.DrawMapEvent;
import com.magenta.rx.java.presenter.MapPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapActivity extends Activity implements OnMapReadyCallback {

    @Inject
    MapPresenter mapPresenter;

    private GoogleMap googleMap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        RXApplication.getInstance().addMapComponent(this);
        EventBus.getDefault().register(this);
        ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    protected void onDestroy() {
        RXApplication.getInstance().removeMapComponent();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    @OnClick({R.id.draw, R.id.clean})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.draw:
                mapPresenter.draw();
                break;
            case R.id.clean:
                mapPresenter.clear();
                break;
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onDraw(DrawMapEvent event) {
        LatLng marker = new LatLng(event.getX(), event.getY());
        googleMap.addMarker(new MarkerOptions().position(marker).title("Random marker"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onClean(CleanMapEvent event) {
        googleMap.clear();
        EventBus.getDefault().removeStickyEvent(event);
    }
}