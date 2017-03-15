package com.magenta.mc.client.android.ui.view;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.magenta.mc.client.android.McAndroidApplication;
import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.db.CacheDBHelper;
import com.magenta.mc.client.android.db.dao.DistributionDAO;
import com.magenta.mc.client.android.db.dao.TileCacheDAO;
import com.magenta.mc.client.android.entity.Address;
import com.magenta.mc.client.android.entity.LocationEntity;
import com.magenta.mc.client.android.entity.MapSettingsEntity;
import com.magenta.mc.client.android.entity.TileCacheEntity;
import com.magenta.mc.client.android.handler.MapUpdateHandler;
import com.magenta.mc.client.android.mc.MxAndroidUtil;
import com.magenta.mc.client.android.service.ServicesRegistry;
import com.magenta.mc.client.android.service.storage.entity.Job;
import com.magenta.mc.client.android.service.storage.entity.Stop;
import com.magenta.mc.client.android.ui.controller.MapController;
import com.magenta.mc.client.android.util.CompressUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Maplet extends WebView {

    public static final int DIALOG_MAP_OPTIONS = 7823645;
    private final static String PAGE_URL = "file:///android_asset/map.html";
    protected boolean mTrackCurrentPosition;
    Boolean isInit = false;
    Activity context;
    MapController mapController;
    MapletJSInterface mapletJSInterface;
    List<Stop> jobs;
    Job run;
    boolean routeWithDriver;
    LocationEntity location;
    MapUpdateHandler handler;
    boolean onLoad;

    public Maplet(Activity context, MapController mapController) {
        super(context);
        this.mapController = mapController;
        initView(context);
    }

    public void setTrackCurrentPosition(boolean track) {
        mTrackCurrentPosition = track;
    }

    protected void initView(final Context context) {
        if (isInit) {
            return;
        }
        this.context = (Activity) context;
        GeoWebChromeClient chromeClient = new GeoWebChromeClient();
        //WebViewClient webClient = new GeoWebViewClient();
        isInit = true;
        getSettings().setJavaScriptEnabled(true);
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        getSettings().setGeolocationEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getSettings().setAllowUniversalAccessFromFileURLs(true);
            getSettings().setAllowFileAccessFromFileURLs(true);
        }
        setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                if (url.equals(PAGE_URL)) {
                    updateSettings();
                }
            }
        });
        setWebChromeClient(chromeClient);
        addJavascriptInterface(getMapletJSInterface(), "mapletJS");
        loadUrl(PAGE_URL);
        handler = new MapletHandler(this);
    }

    public void updateSettings() {
        try {
            List<MapSettingsEntity> entities = DistributionDAO.getInstance().getMapSettings(Settings.get().getLogin());
            if (!entities.isEmpty()) {
                loadUrl("javascript:initConfig(" + entities.get(0).getSettings() + ")");
            }
        } catch (SQLException ignore) {
            loadUrl("javascript:initConfig(" + "" + ")"); //// TODO: 3/12/17 impl
        }
    }

    private Object getMapletJSInterface() {
        if (mapletJSInterface == null) {
            mapletJSInterface = new MapletJSInterface(context);
        }
        return mapletJSInterface;
    }

    public void setJobs(List jobs, boolean routeWithDriver) {
        this.routeWithDriver = routeWithDriver;
        this.jobs = jobs;
        if (this.jobs != null && !this.jobs.isEmpty()) {
            this.run = (Job) this.jobs.get(0).getParentJob();
        }
    }

    public void showCurrentPosition() {
        location = MxAndroidUtil.getGeoLocation();
        if (location != null) {
            Maplet.this.loadUrl("javascript:showCurrentPosition(" + location.getLat() + "," + location.getLon() + ")");
        }
    }

    public void showJobs() {
        for (Stop job : jobs) {
            if (job.getState() < 0) break;
            Address address = job.getAddress();
            String[] times = job.getTimeAsString().split(":");
            Maplet.this.loadUrl("javascript:showOrder(" + address.getLatitude() + ","
                    + address.getLongitude() + ","
                    + job.isPickup() + ","
                    + job.getPriority() + ","
                    + "'" + job.getReferenceId() + "'"
                    + ",'" + times[0] + "','" + times[1] + "')");
        }
    }

    public void showDC() {
        Address address = run.getAddress();
        Maplet.this.loadUrl("javascript:showDC(" + address.getLatitude() + "," + address.getLongitude() + ")");
    }

    public boolean showEnd() {
        Address address = run.getEndAddress();
        if (address != null) {
            Maplet.this.loadUrl("javascript:showEnd(" + address.getLatitude() + "," + address.getLongitude() + ")");
            return true;
        }
        return false;
    }

    public boolean showStart() {
        Address address = run.getStartAddress();
        if (address != null) {
            Maplet.this.loadUrl("javascript:showStart(" + address.getLatitude() + "," + address.getLongitude() + ")");
            return true;
        }
        return false;
    }

    public void updateRoute(String route) {
        Maplet.this.loadUrl("javascript:showRunRoute(" + route + ")");
    }

    public void fitBounds(String bounds) {
        Maplet.this.loadUrl("javascript:fitBounds(" + bounds + ")");
    }

    public void stopRunnable() {
        handler.stop();
    }

    public void resumeRunnable() {
        if (onLoad) {
            handler.start();
        }
    }

    public void zoomInJs() {
        Maplet.this.loadUrl("javascript:zoomIn()");
    }

    public void zoomOutJs() {
        Maplet.this.loadUrl("javascript:zoomOut()");
    }

    public void myLocationJs() {
        Location loc = ServicesRegistry.getLocationService().getLocation();
        if (loc != null) {
            Maplet.this.loadUrl("javascript:panToCurrent(" + loc.getLatitude() + "," + loc.getLongitude() + ")");
        }
    }

    static class MapletHandler extends MapUpdateHandler {

        final Maplet maplet;

        MapletHandler(Maplet maplet) {
            this.maplet = maplet;
        }

        protected void updateMap(boolean firstRun) {
            LocationEntity loc = MxAndroidUtil.getGeoLocation();
            if (loc != null && maplet.mapController.mTrackCurrentPosition) {
                maplet.loadUrl("javascript:panToCurrent(" + loc.getLat() + "," + loc.getLon() + ")");
            }
            if (firstRun) {
                List<Address> addressList = new ArrayList<>();
                if (maplet.routeWithDriver) {
                    if (loc != null) {
                        if (maplet.location != null && !(firstRun) && maplet.location.getLat().equals(loc.getLat()) && maplet.location.getLon().equals(loc.getLon())) {
                            maplet.location = loc;
                            return;
                        }
                        maplet.location = loc;
                    } else {
                        if (!firstRun) return;
                    }
                    if (maplet.location != null) {
                        Address address = new Address();
                        address.setLatitude(maplet.location.getLat());
                        address.setLongitude(maplet.location.getLon());
                        addressList.add(address);
                    }
                    for (Stop stop : maplet.jobs) {
                        addressList.add(stop.getAddress());
                    }
                } else if (firstRun) {
                    addressList.add(maplet.run.getStartAddress() != null ? maplet.run.getStartAddress() : maplet.run.getAddress());
                    for (Stop stop : maplet.jobs) {
                        addressList.add(stop.getAddress());
                    }
                    addressList.add(maplet.run.getEndAddress() != null ? maplet.run.getEndAddress() : maplet.run.getAddress());
                }
                maplet.mapController.sendUpdateRequest(addressList);
            }
        }
    }

    private class GeoWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public class GeoWebChromeClient extends WebChromeClient {
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
        }
    }

    public class MapletJSInterface {
        Context context;

        public MapletJSInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void getCurrentLocation() {
            handler.post(new Runnable() {
                public void run() {
                    showCurrentPosition();
                }
            });
        }

        @JavascriptInterface
        public void getJobs() {
            handler.post(new Runnable() {
                public void run() {
                    showJobs();
                }
            });
        }

        @JavascriptInterface
        public void onload() {
            handler.start();
            onLoad = true;
            handler.post(new Runnable() {
                public void run() {
                    mapController.onMapReady();
                }
            });
        }

        @JavascriptInterface
        public void showSettingsDialog() {
            ((Activity) context).showDialog(DIALOG_MAP_OPTIONS);
        }

        @JavascriptInterface
        public void onJobTap(String referenceId) {
            Stop stop = null;
            for (Stop job : jobs) {
                if (job.getReferenceId().equalsIgnoreCase(referenceId)) {
                    stop = job;
                    break;
                }
            }
            if (stop == null) {
                return;
            }
            mapController.onJobTap(stop);
        }

        @JavascriptInterface
        public void onDCTap() {
            Job job = (Job) ServicesRegistry.getDataController().findJob(run.getReferenceId());
            if (job != null) {
                mapController.onDCTap(job);
            }
        }

        @JavascriptInterface
        public void onEndTap() {
            Job job = (Job) ServicesRegistry.getDataController().findJob(run.getReferenceId());
            if (job != null) {
                mapController.onEndTap(job);
            }
        }

        @JavascriptInterface
        public void onStartTap() {
            Job job = (Job) ServicesRegistry.getDataController().findJob(run.getReferenceId());
            if (job != null) {
                mapController.onStartTap(job);
            }
        }

        @JavascriptInterface
        public void getTileInCache(final String url, final String name, final String x, final String y, final String z) {
            String blob = "";
            try {
                List<TileCacheEntity> tiles = TileCacheDAO.getInstance().getTileFromCache(name != null ? name : "default", Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(z));
                if (!tiles.isEmpty()) {
                    blob = new String(CompressUtils.decompress(tiles.get(0).getBlob()), Charset.forName("US-ASCII"));
                    TileCacheDAO.getInstance().updateUsedDate(tiles.get(0));
                }
            } catch (Exception ignore) {
            }
            final String src = blob;
            handler.post(new Runnable() {
                public void run() {
                    if (!src.isEmpty()) {
                        Maplet.this.loadUrl("javascript:callbacks[" + url.hashCode() + "]('data:image/png;base64," + src + "')");
                    } else {
                        Maplet.this.loadUrl("javascript:callbacks[" + url.hashCode() + "]('" + "" + "')");
                    }
                }
            });
        }

        @JavascriptInterface
        public void saveTile(final String blob, final String name, final String x, final String y, final String z) {
            try {
                TileCacheEntity entity = new TileCacheEntity();
                entity.setLastAccessDate(System.currentTimeMillis());
                entity.setProvider(name);
                entity.setX(Integer.valueOf(x));
                entity.setY(Integer.valueOf(y));
                entity.setZ(Integer.valueOf(z));
                entity.setBlob(CompressUtils.compress(blob.getBytes(Charset.forName("US-ASCII"))));
                TileCacheDAO.getInstance().saveTileToCache(entity);
                if (new File(McAndroidApplication.getInstance().getDBAdapter().getDB(CacheDBHelper.DATABASE_NAME).getPath()).length() >=
                        500 * 1024 * 1024) { // TODO: 3/12/17 impl
                    TileCacheDAO.getInstance().removeCacheTiles(null);
                }
            } catch (Exception ignore) {
            }
        }
    }
}