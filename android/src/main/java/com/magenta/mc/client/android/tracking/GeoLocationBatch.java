package com.magenta.mc.client.android.tracking;

import com.magenta.mc.client.android.resender.Resendable;
import com.magenta.mc.client.android.resender.ResendableMetadata;
import com.magenta.mc.client.android.storage.FieldGetter;
import com.magenta.mc.client.android.storage.FieldSetter;
import com.magenta.mc.client.android.storage.StorableMetadata;

import java.util.ArrayList;
import java.util.List;

public class GeoLocationBatch extends Resendable {

    public static final ResendableMetadata METADATA = new ResendableMetadata("locations", true, true, false, true);

    private static final long serialVersionUID = 1L;

    private final String FIELD_ID = "id";
    private final String FIELD_LOCATIONS = "locations";

    private String id;
    private List<GeoLocation> locations = new ArrayList<>(); //list of GeoLocation

    public GeoLocationBatch() {
    }

    public GeoLocationBatch(List<GeoLocation> locations) {
        this.locations = locations;
    }

    public StorableMetadata getMetadata() {
        return METADATA;
    }

    public boolean send() {
//        HttpClient.getInstance().sendLocations(id, locations);
        return true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List getLocations() {
        return locations;
    }

    public FieldSetter[] getSetters() {
        return new FieldSetter[]{
                new FieldSetter(FIELD_ID) {
                    public void setValue(Object value) {
                        id = (String) value;
                    }
                },
                new FieldSetter(FIELD_LOCATIONS) {
                    public void setValue(Object value) {
                        locations = (List<GeoLocation>) value;
                    }
                }
        };
    }

    public FieldGetter[] getGetters() {
        return new FieldGetter[]{
                new FieldGetter(FIELD_ID) {
                    public Object getValue() {
                        return id;
                    }
                },
                new FieldGetter(FIELD_LOCATIONS) {
                    public Object getValue() {
                        return locations;
                    }
                }
        };
    }
}