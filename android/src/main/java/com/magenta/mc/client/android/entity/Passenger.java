package com.magenta.mc.client.android.entity;

import com.magenta.mc.client.android.mc.storage.FieldGetter;
import com.magenta.mc.client.android.mc.storage.FieldSetter;
import com.magenta.mc.client.android.mc.storage.StorableField;

public class Passenger extends StorableField {

    private static final long serialVersionUID = 4;
    private static final String FIELD_PICKUP = "pickup";
    private static final String FIELD_DROP = "drop";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_PHONE_1 = "phone1";
    private static final String FIELD_PHONE_2 = "phone2";
    private String name;
    private String phone1;
    private String phone2;
    private Integer pickup;
    private Integer drop;

    public Passenger(String name, String phone1, String phone2, Integer pickup, Integer drop) {
        this.name = name;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.pickup = pickup;
        this.drop = drop;
    }

    public Passenger() {
    }

    public String getName() {
        return name;
    }

    public String getPhone1() {
        return phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public Integer getPickup() {
        return pickup;
    }

    public Integer getDrop() {
        return drop;
    }

    public String toString() {
        String result = name;
        if (phone1 != null && phone1.trim().length() > 0)
            result += ", " + phone1;
        if (phone2 != null && phone2.trim().length() > 0)
            result += ", " + phone2;
        return result;
    }

    public FieldSetter[] getSetters() {
        return new FieldSetter[]{
                new FieldSetter(FIELD_NAME) {
                    public void setValue(Object value) {
                        name = (String) value;
                    }
                },
                new FieldSetter(FIELD_PHONE_1) {
                    public void setValue(Object value) {
                        phone1 = (String) value;
                    }
                },
                new FieldSetter(FIELD_PHONE_2) {
                    public void setValue(Object value) {
                        phone2 = (String) value;
                    }
                },
                new FieldSetter(FIELD_PICKUP) {
                    public void setValue(Object value) {
                        pickup = (Integer) value;
                    }
                },
                new FieldSetter(FIELD_DROP) {
                    public void setValue(Object value) {
                        drop = (Integer) value;
                    }
                }
        };
    }

    public FieldGetter[] getGetters() {
        return new FieldGetter[]{
                new FieldGetter(FIELD_NAME) {
                    public Object getValue() {
                        return name;
                    }
                },
                new FieldGetter(FIELD_PHONE_1) {
                    public Object getValue() {
                        return phone1;
                    }
                },
                new FieldGetter(FIELD_PHONE_2) {
                    public Object getValue() {
                        return phone2;
                    }
                },
                new FieldGetter(FIELD_PICKUP) {
                    public Object getValue() {
                        return pickup;
                    }
                },
                new FieldGetter(FIELD_DROP) {
                    public Object getValue() {
                        return drop;
                    }
                }
        };
    }
}