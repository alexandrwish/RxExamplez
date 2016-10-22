package com.magenta.rx.rxa.model.loader;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;

import com.magenta.rx.rxa.RXApplication;
import com.magenta.rx.rxa.binder.LocalBinder;
import com.magenta.rx.rxa.model.entity.GeoLocationEntity;
import com.magenta.rx.rxa.service.GeoLocationService;

import org.greenrobot.greendao.rx.RxDao;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class ServiceLoader {

    private GeoLocationService locationService;

    public ServiceLoader() {
        Context context = RXApplication.getInstance();
        context.bindService(new Intent(context, GeoLocationService.class), new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                locationService = (GeoLocationService) ((LocalBinder) service).getService();
            }

            public void onServiceDisconnected(ComponentName className) {
                locationService = null;
            }
        }, Context.BIND_AUTO_CREATE);
    }

    public Observable<Location> load() {
        return Observable.merge(
                locationService.getLocations()
                        .doOnNext(new Action1<Location>() {
                            public void call(Location location) {
                                RXApplication.getInstance().getSession().getGeoLocationEntityDao().insert(new GeoLocationEntity(null, location.getLatitude(), location.getLongitude(), location.getTime()));
                            }
                        }),
                new RxDao<>(RXApplication.getInstance().getSession().getGeoLocationEntityDao()).loadAll()
                        .flatMap(new Func1<List<GeoLocationEntity>, Observable<GeoLocationEntity>>() {
                            public Observable<GeoLocationEntity> call(List<GeoLocationEntity> geoLocationEntities) {
                                return Observable.from(geoLocationEntities);
                            }
                        })
                        .map(new Func1<GeoLocationEntity, Location>() {
                            public Location call(GeoLocationEntity geoLocationEntity) {
                                Location location = new Location("gps");
                                location.setLatitude(geoLocationEntity.getLat());
                                location.setLongitude(geoLocationEntity.getLon());
                                location.setTime(geoLocationEntity.getTimestamp());
                                return location;
                            }
                        }));
    }
}