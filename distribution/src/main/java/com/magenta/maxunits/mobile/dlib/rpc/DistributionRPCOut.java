package com.magenta.maxunits.mobile.dlib.rpc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.magenta.maxunits.distribution.R;
import com.magenta.maxunits.mobile.MxApplication;
import com.magenta.maxunits.mobile.dlib.activity.DistributionActivity;
import com.magenta.maxunits.mobile.dlib.db.dao.DistributionDAO;
import com.magenta.maxunits.mobile.dlib.dialog.DialogFactory;
import com.magenta.maxunits.mobile.dlib.dialog.DistributionDialogFragment;
import com.magenta.maxunits.mobile.dlib.entity.MapProviderType;
import com.magenta.maxunits.mobile.dlib.entity.MapSettingsEntity;
import com.magenta.maxunits.mobile.entity.Address;
import com.magenta.maxunits.mobile.mc.MxSettings;
import com.magenta.maxunits.mobile.rpc.RPCOut;
import com.magenta.mc.client.android.ui.AndroidUI;
import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.xml.XMLDataBlock;
import com.magenta.mc.client.xmpp.extensions.rpc.JabberRPC;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class DistributionRPCOut extends RPCOut {

    public static final String UPDATE_ROUTE = "updateRoute";
    public static final String SAVE_STATE = "savePhoneState";
    public static final String UPDATE_ROUTE_ACTION = "update_route";

    static DistributionRPCOut instance;

    protected DistributionRPCOut() {
    }

    public static DistributionRPCOut getInstance() {
        return instance == null ? (instance = new DistributionRPCOut()) : instance;
    }

    @SuppressWarnings("unused")
    public static void accountConfigurationResponse(Long id, XMLDataBlock data) {
        XMLDataBlock stringBlock = data.getChildBlock("string");
        XMLDataBlock response = stringBlock.getChildBlock("accountConfig");
        XMLDataBlock config = response.getChildBlock("config");
        for (Enumeration e = config.getChildBlocks().elements(); e.hasMoreElements(); ) {
            XMLDataBlock child = (XMLDataBlock) e.nextElement();
            if (child.getTagName().equals("mobile.maxunites.allow.to.pass.in.arbitrary.order")) {
                boolean isAllowToPassInArbitraryOrder = Boolean.valueOf(child.getText());
                Settings.get().setProperty("allowToPassStopsInArbitraryOrder", Boolean.toString(isAllowToPassInArbitraryOrder));
                continue;
            } else if (child.getTagName().equals("mobile.maxunites.allow.to.pass.several.runs")) {
                boolean isAllowToPassInArbitraryOrder = Boolean.valueOf(child.getText());
                Settings.get().setProperty("allowToPassJobsInArbitraryOrder", Boolean.toString(isAllowToPassInArbitraryOrder));
                continue;
            } else if (child.getTagName().equalsIgnoreCase("orderCancelReasons")) {
                Vector childBlocks = child.getChildBlocks();
                if (childBlocks != null && childBlocks.size() > 0) {
                    ArrayList<String> cancelReasons = new ArrayList<String>();
                    for (Object o : childBlocks) {
                        XMLDataBlock dataBlock = (XMLDataBlock) o;
                        if (dataBlock.getChildBlocks() != null) {
                            cancelReasons.add(dataBlock.getChildBlock("text").getText());
                        }
                    }
                    MxSettings.getInstance().setOrderCancelReasons(cancelReasons);
                }
                continue;
            } else if (child.getTagName().equalsIgnoreCase("map.property")) {
                HashMap<String, HashMap<String, String>> mapSettings = new HashMap<String, HashMap<String, String>>();
                for (Object o : child.getChildBlocks()) {
                    XMLDataBlock dataBlock = (XMLDataBlock) o;
                    HashMap<String, String> providerSettings = new HashMap<String, String>();
                    if (dataBlock.getChildBlocks() != null) {
                        for (Object o1 : dataBlock.getChildBlocks()) {
                            XMLDataBlock settings = (XMLDataBlock) o1;
                            providerSettings.put(settings.getTagName(), settings.getText());
                        }
                    } else {
                        providerSettings.put(dataBlock.getTagName(), dataBlock.getText());
                    }
                    mapSettings.put(dataBlock.getTagName(), providerSettings);
                }
                mapSettings = deleteUnUsedMaps(mapSettings);
                Settings.get().setProperty(child.getTagName(), new Gson().toJson(mapSettings));
                Activity activity = ((AndroidUI) Setup.get().getUI()).getCurrentActivity();
                if (activity != null && activity instanceof DistributionActivity) {
                    try {
                        List<MapSettingsEntity> mapSettingsEntities = DistributionDAO.getInstance(activity).getMapSettings(Setup.get().getSettings().getLogin());
                        if (!mapSettingsEntities.isEmpty()) {
                            if (mapSettings.containsKey(mapSettingsEntities.get(0).getProvider())) {
                                if (mapSettingsEntities.get(0).isRemember()) {
                                    continue;
                                }
                            } else {
                                if (updateSettings(activity, mapSettingsEntities.get(0), mapSettings)) {
                                    Bundle bundle = new Bundle(3);
                                    bundle.putInt(DialogFactory.ICON, android.R.drawable.ic_dialog_info);
                                    bundle.putInt(DialogFactory.TITLE, R.string.alert_map_title);
                                    bundle.putInt(DialogFactory.VALUE, R.string.alert_map_value);
                                    DistributionDialogFragment fragment = DialogFactory.create(DialogFactory.ALERT_DIALOG, bundle);
                                    if (fragment != null) {
                                        fragment.show(activity.getFragmentManager(), fragment.getName());
                                    }
                                    continue;
                                }
                            }
                        } else {
                            updateSettings(activity, new MapSettingsEntity(), mapSettings);
                        }
                    } catch (SQLException exception) {
                        LOG.error(exception.getMessage(), exception);
                    }
                    ((DistributionActivity) activity).updateMapSettings();
                }
                continue;
            }
            Settings.get().setProperty(child.getTagName(), child.getText());
        }
        Settings.get().saveSettings();
    }

    static HashMap<String, HashMap<String, String>> deleteUnUsedMaps(HashMap<String, HashMap<String, String>> mapSettings) {
        if (mapSettings == null) {
            mapSettings = new HashMap<String, HashMap<String, String>>(1);
        } else {
            for (String s : MxSettings.ignoredMapProviders) {
                mapSettings.remove(s);
            }
        }
        if (mapSettings.isEmpty()) {
            HashMap<String, String> osm = new HashMap<String, String>(1);
            osm.put("use_map_provider", "true");
            mapSettings.put("openstreetmap", osm);
        }
        return mapSettings;
    }

    static boolean updateSettings(Activity activity, MapSettingsEntity entity, HashMap<String, HashMap<String, String>> mapSettings) throws SQLException {
        if (mapSettings.size() == 1) {
            entity.setDriver(Setup.get().getSettings().getLogin());
            for (Map.Entry<String, HashMap<String, String>> entry : mapSettings.entrySet()) {
                entity.setProvider(entry.getKey());
                entity.setMapProviderType(MapProviderType.GOOGLE.name().equalsIgnoreCase(entry.getKey())
                        ? MapProviderType.GOOGLE
                        : (MapProviderType.YANDEX.name().equalsIgnoreCase(entry.getKey())
                        ? MapProviderType.YANDEX
                        : MapProviderType.LEAFLET));
            }
            entity.setSettings(new Gson().toJson(mapSettings));
            DistributionDAO.getInstance(activity).saveMapSettings(entity);
            return true;
        }
        return false;
    }

    public static void updateRoute(List<Address> addresses, Long synchronizeTimestamp) {
        Object[][] locations = new Object[addresses.size() + 1][2];
        for (int i = 0; i < addresses.size(); i++) {
            locations[i] = new Object[]{addresses.get(i).getLatitude(), addresses.get(i).getLongitude()};
        }
        locations[addresses.size()] = new String[]{};
        JabberRPC.getInstance().call(UPDATE_ROUTE, new Object[]{locations}, synchronizeTimestamp);
    }

    @SuppressWarnings("unused")
    public static void updateRouteResponse(Long id, XMLDataBlock data) {
        Bundle bundle = new Bundle();
        bundle.putString("route", data.getChildBlock("string").getChildBlock("route").getText());
        bundle.putLong("synchronizeTimestamp", id);
        ((AndroidUI) Setup.get().getUI()).getCurrentActivity().sendBroadcast(new Intent(UPDATE_ROUTE_ACTION).putExtras(bundle));
    }

    public static void savePhoneState(String phoneStatistics, Date date) {
        JabberRPC.getInstance().call(SAVE_STATE, new Object[]{phoneStatistics}, date.getTime());
    }

    @SuppressWarnings("unused")
    public static void savePhoneStateResponse(final Long id, final XMLDataBlock data) {
        if (data.getChildBlock("string").getChildBlock("update").getText().equalsIgnoreCase("success")) {
            MxApplication.getInstance().completeStatisticSending(new Date(id));
        } else {
            LOG.error("Cannot save state on sever");
        }
    }
}