package com.magenta.mc.client.android.entity;

import com.magenta.mc.client.android.mc.storage.FieldGetter;
import com.magenta.mc.client.android.mc.storage.FieldSetter;
import com.magenta.mc.client.android.mc.storage.StorableField;

public class Parcel extends StorableField {

    private static final long serialVersionUID = 3;

    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_QUANTITY = "quantity";
    private static final String FIELD_PICKUP = "pickup";
    private static final String FIELD_DROP = "drop";

    private String description;
    private Integer quantity;
    private Integer pickup;
    private Integer drop;

    public Parcel(String description, Integer quantity, Integer pickup, Integer drop) {
        this.description = description;
        this.quantity = quantity;
        this.pickup = pickup;
        this.drop = drop;
    }

    public Parcel() {
    }

    public String getDescription() {
        return description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getPickup() {
        return pickup;
    }

    public Integer getDrop() {
        return drop;
    }

    public String toString() {
        return description + (quantity != null ? ", " + quantity + " units" : "");
    }

    public FieldSetter[] getSetters() {
        return new FieldSetter[]{
                new FieldSetter(FIELD_DESCRIPTION) {
                    public void setValue(Object value) {
                        description = (String) value;
                    }
                },
                new FieldSetter(FIELD_QUANTITY) {
                    public void setValue(Object value) {
                        quantity = (Integer) value;
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
                new FieldGetter(FIELD_DESCRIPTION) {
                    public Object getValue() {
                        return description;
                    }
                },
                new FieldGetter(FIELD_QUANTITY) {
                    public Object getValue() {
                        return quantity;
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