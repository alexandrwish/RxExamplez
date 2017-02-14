package com.magenta.mc.client.android.mc.tracking;

import com.magenta.mc.client.android.mc.log.ServerTime;
import com.magenta.mc.client.android.mc.storage.FieldGetter;
import com.magenta.mc.client.android.mc.storage.FieldSetter;
import com.magenta.mc.client.android.mc.storage.StorableField;

import java.util.Date;

/**
 * @author Petr Popov
 *         Created: 06.12.11 19:07
 */
public class GeoLocation extends StorableField {

    private static final long serialVersionUID = 10L;

    private static final String FIELD_STATE = "state";
    private static final String FIELD_SOURCE = "source";

    private static final String FIELD_USER_ID = "userId";

    private static final String FIELD_LAT = "lat";
    private static final String FIELD_LON = "lon";
    private static final String FIELD_TIMESTAMP = "timestamp";
    private static final String FIELD_RETRIEVE_TIMESTAMP = "retrieveTimestamp";
    private static final String FIELD_SPEED = "speed";
    private static final String FIELD_HEADING = "heading";
    private static final String FIELD_SATTELITE_COUNT = "satelliteCount";

    //mc realization of jabber-rpc does not support null strings
    private String userId = "";

    private GeoLocationState state;

    private GeoLocationSource source = GeoLocationSource.UNKNOWN;

    private Double lat;

    private Double lon;

    private Long timestamp;

    private Long retrieveTimestamp;

    private Float speed;

    private Float heading;

    private Integer satelliteCount;

    public GeoLocation() {
    }

    public GeoLocation(Long timestamp, Double lat, Double lon, Float speed, Float heading, Integer satelliteCount) {
        this.timestamp = timestamp;
        this.lat = lat;
        this.lon = lon;
        this.speed = speed;
        this.heading = heading;
        this.satelliteCount = satelliteCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId != null ? userId : "";
    }

    public GeoLocationState getState() {
        return state;
    }

    public void setState(GeoLocationState state) {
        this.state = state;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Long getRetrieveTimestamp() {
        return retrieveTimestamp;
    }

    public void setRetrieveTimestamp(Long retrieveTimestamp) {
        this.retrieveTimestamp = retrieveTimestamp;
    }

    public Float getSpeed() {
        return speed;
    }

    public Float getHeading() {
        return heading;
    }

    public Integer getSatelliteCount() {
        return satelliteCount;
    }

    public GeoLocationSource getSource() {
        return source;
    }

    public void setSource(GeoLocationSource source) {
        this.source = source;
    }

    public String toString() {
        return new StringBuffer("GL{")
                .append("state=").append(state)
                .append(", src=").append(source)
                .append(", hd=").append(heading)
                .append(", sp=").append(speed)
                .append(", tss=").append(ServerTime.get(timestamp.longValue()))
                .append(", st=").append(satelliteCount)
                .append(", usr=").append(userId)
                .append(", lt=").append(lat)
                .append(", ln=").append(lon)
                .append(", ts=").append((timestamp != null) ? new Date(timestamp.longValue()) : null)
                .append('}').toString();
    }

    public FieldSetter[] getSetters() {
        return new FieldSetter[]{
                new FieldSetter(FIELD_USER_ID) {
                    public void setValue(Object value) {
                        userId = (String) value;
                    }
                },
                new FieldSetter(FIELD_STATE) {
                    public void setValue(Object value) {
                        state = (GeoLocationState) value;
                    }
                },
                new FieldSetter(FIELD_LAT) {
                    public void setValue(Object value) {
                        lat = (Double) value;
                    }
                },
                new FieldSetter(FIELD_LON) {
                    public void setValue(Object value) {
                        lon = (Double) value;
                    }
                },
                new FieldSetter(FIELD_TIMESTAMP) {
                    public void setValue(Object value) {
                        timestamp = (Long) value;
                    }
                },
                new FieldSetter(FIELD_RETRIEVE_TIMESTAMP) {
                    public void setValue(Object value) {
                        retrieveTimestamp = (Long) value;
                    }
                },
                new FieldSetter(FIELD_SPEED) {
                    public void setValue(Object value) {
                        speed = (Float) value;
                    }
                },
                new FieldSetter(FIELD_HEADING) {
                    public void setValue(Object value) {
                        heading = (Float) value;
                    }
                },
                new FieldSetter(FIELD_SATTELITE_COUNT) {
                    public void setValue(Object value) {
                        satelliteCount = (Integer) value;
                    }
                },
                new FieldSetter(FIELD_SOURCE) {
                    public void setValue(Object value) {
                        source = (GeoLocationSource) value;
                    }
                }
        };
    }

    public FieldGetter[] getGetters() {
        return new FieldGetter[]{
                new FieldGetter(FIELD_USER_ID) {
                    public Object getValue() {
                        return userId;
                    }
                },
                new FieldGetter(FIELD_STATE) {
                    public Object getValue() {
                        return state;
                    }
                },
                new FieldGetter(FIELD_LAT) {
                    public Object getValue() {
                        return lat;
                    }
                },
                new FieldGetter(FIELD_LON) {
                    public Object getValue() {
                        return lon;
                    }
                },
                new FieldGetter(FIELD_TIMESTAMP) {
                    public Object getValue() {
                        return timestamp;
                    }
                },
                new FieldGetter(FIELD_RETRIEVE_TIMESTAMP) {
                    public Object getValue() {
                        return retrieveTimestamp;
                    }
                },
                new FieldGetter(FIELD_SPEED) {
                    public Object getValue() {
                        return speed;
                    }
                },
                new FieldGetter(FIELD_HEADING) {
                    public Object getValue() {
                        return heading;
                    }
                },
                new FieldGetter(FIELD_SATTELITE_COUNT) {
                    public Object getValue() {
                        return satelliteCount;
                    }
                },
                new FieldGetter(FIELD_SOURCE) {
                    public Object getValue() {
                        return source;
                    }
                }
        };
    }
}
