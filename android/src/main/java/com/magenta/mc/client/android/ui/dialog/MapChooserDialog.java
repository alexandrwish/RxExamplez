package com.magenta.mc.client.android.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.db.dao.DistributionDAO;
import com.magenta.mc.client.android.entity.MapSettingsEntity;
import com.magenta.mc.client.android.entity.type.MapProviderType;
import com.magenta.mc.client.android.util.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapChooserDialog extends DistributionDialogFragment {

    MapSettingsEntity entity;
    Map mapSettings;
    boolean emptySettings = true;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View mapChooser = getActivity().getLayoutInflater().inflate(R.layout.view_choose_map, null);
        Bundle bundle = getArguments();
        String driver = Settings.get().getLogin();
        mapSettings = (Map) bundle.getSerializable("map.property");
        final ArrayList<String> mapProviders = new ArrayList<>(mapSettings.keySet());
        try {
            List<MapSettingsEntity> settingsEntities = DistributionDAO.getInstance().getMapSettings(driver);
            if (settingsEntities.isEmpty()) {
                entity = new MapSettingsEntity();
                entity.setDriver(driver);
            } else {
                if (settingsEntities.size() != 1) {
                    LOG.error(String.format(Locale.UK, "Settings size = %d for driver %s", settingsEntities.size(), driver));
                }
                entity = settingsEntities.get(0);
                emptySettings = false;
            }
        } catch (SQLException ignore) {
            entity = new MapSettingsEntity();
            entity.setDriver(driver);
            LOG.info(String.format("Create default settings for driver: %s", driver));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, mapProviders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner mapSpinner = ((Spinner) mapChooser.findViewById(R.id.map_spinner));
        mapSpinner.setAdapter(adapter);
        mapSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fillEntity(mapProviders.get(i));
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        if (!StringUtils.isBlank(entity.getProvider())) {
            int index = 0;
            for (String provider : mapProviders) {
                if (provider.equalsIgnoreCase(entity.getProvider())) break;
                index++;
            }
            if (mapProviders.size() != index) {
                mapSpinner.setSelection(index, true);
            }
        }
        final CheckBox remember = ((CheckBox) mapChooser.findViewById(R.id.map_remember));
        remember.setChecked(entity.isRemember());
        remember.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                remember.setChecked(remember.isChecked());
                entity.setRemember(remember.isChecked());
            }
        });
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.map_dialog)
                .setView(mapChooser)
                .setPositiveButton(R.string.mx_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                try {
                                    DistributionDAO.getInstance().saveMapSettings(entity);
                                } catch (SQLException ignore) {
                                }
                            }
                        }
                )
                .setNegativeButton(R.string.mx_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                setDefaultMap();
                            }
                        }
                ).create();
    }

    public void onCancel(DialogInterface dialog) {
        setDefaultMap();
        super.onCancel(dialog);
    }

    public String getName() {
        return "MapChooserDialog";
    }

    private void fillEntity(String provider) {
        if (MapProviderType.GOOGLE.name().equalsIgnoreCase(provider)) {
            entity.setMapProviderType(MapProviderType.GOOGLE);
        } else if (MapProviderType.YANDEX.name().equalsIgnoreCase(provider)) {
            entity.setMapProviderType(MapProviderType.YANDEX);
        } else {
            entity.setMapProviderType(MapProviderType.LEAFLET);
            Map<String, Map<String, String>> setting = new HashMap<>();
            setting.put(provider, (Map) mapSettings.get(provider));
            entity.setSettings(new Gson().toJson(setting));
        }
        entity.setProvider(provider);
    }

    private void setDefaultMap() {
        if (!emptySettings) {
            return;
        }
        Object o = "google"; // TODO: 3/12/17 impl
        if (o != null && !StringUtils.isBlank((String) o)) {
            String provider = ((String) o).toLowerCase();
            for (String s : Settings.IGNORED_MAP_PROVIDERS) {
                if (s.equalsIgnoreCase(provider)) {
                    provider = "openstreetmap";
                    break;
                }
            }
            fillEntity(provider);
            try {
                DistributionDAO.getInstance().saveMapSettings(entity);
            } catch (SQLException ignore) {
            }
        }
    }
}