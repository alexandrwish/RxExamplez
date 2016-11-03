package com.magenta.maxunits.mobile.dlib.utils;

import android.graphics.drawable.Drawable;

import com.magenta.maxunits.mobile.dlib.entity.DynamicAttributeType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Attribute {

    private Integer id;
    private String title;
    private String unit;
    private Boolean required;
    private Boolean editable;
    private String value;
    private Drawable icon;
    private DynamicAttributeType type;
    private List listeners = new ArrayList();

    public Attribute(Integer id, String title, String unit, Boolean required, Boolean editable, String value, Drawable icon, DynamicAttributeType type) {
        this.id = id;
        this.title = title;
        this.unit = unit;
        this.required = required;
        this.editable = editable;
        this.value = value;
        this.icon = icon;
        this.type = type;
    }

    public Attribute(String title, String value) {
        this(null, title, null, false, false, value, null, DynamicAttributeType.STRING);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Attribute setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getUnit() {
        return unit;
    }

    public Attribute setUnit(String unit) {
        this.unit = unit;
        return this;
    }

    public Boolean isRequired() {
        return required;
    }

    public Attribute setRequired(Boolean required) {
        this.required = required;
        return this;
    }

    public Boolean isEditable() {
        return editable;
    }

    public Attribute setEditable(Boolean editable) {
        this.editable = editable;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Attribute setValue(String value) {
        this.value = value;
        return this;
    }

    public Drawable getIcon() {
        return icon;
    }

    public Attribute setIcon(Drawable icon) {
        this.icon = icon;
        return this;
    }

    public DynamicAttributeType getType() {
        return type;
    }

    public Attribute setType(DynamicAttributeType type) {
        this.type = type;
        return this;
    }

    public List getListeners() {
        return listeners;
    }

    public Attribute setListeners(List listeners) {
        this.listeners = listeners;
        return this;
    }

    public Attribute addListeners(Object... objects) {
        Collections.addAll(listeners, objects);
        return this;
    }
}