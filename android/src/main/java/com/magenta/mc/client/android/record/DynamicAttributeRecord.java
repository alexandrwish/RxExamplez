package com.magenta.mc.client.android.record;

import com.google.gson.Gson;
import com.magenta.mc.client.android.entity.DynamicAttributeEntity;
import com.magenta.mc.client.android.entity.DynamicAttributeType;
import com.magenta.mc.client.android.entity.LocalizeStringEntity;

import java.io.Serializable;

public class DynamicAttributeRecord implements Serializable {

    private long mxId;

    private boolean pdaEditable;

    private boolean pdaRequired;

    private String value;

    private String name;

    private String unit;

    private String title;

    private DynamicAttributeType typeName;

    public DynamicAttributeRecord(long mxId, String value) {
        this.mxId = mxId;
        this.value = value;
    }

    public long getMxId() {
        return mxId;
    }

    public void setMxId(long mxId) {
        this.mxId = mxId;
    }

    public boolean isPdaEditable() {
        return pdaEditable;
    }

    public void setPdaEditable(boolean pdaEditable) {
        this.pdaEditable = pdaEditable;
    }

    public boolean isPdaRequired() {
        return pdaRequired;
    }

    public void setPdaRequired(boolean pdaRequired) {
        this.pdaRequired = pdaRequired;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DynamicAttributeType getTypeName() {
        return typeName;
    }

    public void setTypeName(DynamicAttributeType type) {
        this.typeName = type;
    }

    public DynamicAttributeEntity toEntity() {
        DynamicAttributeEntity entity = new DynamicAttributeEntity();
        entity.setMxID(String.valueOf(mxId));
        entity.setPdaEditable(pdaEditable);
        entity.setPdaRequired(pdaRequired);
        entity.setName(name);
        entity.setValue(value);
        entity.setUnit(unit);
        entity.setTitle(new Gson().fromJson(title, LocalizeStringEntity.class));
        entity.setTypeName(typeName);
        return entity;
    }
}