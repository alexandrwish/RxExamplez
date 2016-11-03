package com.magenta.maxunits.mobile.entity;

import com.magenta.mc.client.storage.FieldGetter;
import com.magenta.mc.client.storage.FieldSetter;
import com.magenta.mc.client.storage.StorableField;

public class Attribute extends StorableField {

    private static final long serialVersionUID = -7405447805133848573L;

    private static final String FIELD_TITLE = "title";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_TYPE_NAME = "typeName";
    private static final String FIELD_VALUE = "value";
    private static final String FIELD_UNIT = "unit";

    private String title;
    private String name;
    private String typeName;
    private String value;
    private String unit;

    public Attribute() {
    }

    public Attribute(final String title, final String name, final String typeName, final String value, final String unit) {
        this.title = title;
        this.name = name;
        this.typeName = typeName;
        this.value = value;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(final String typeName) {
        this.typeName = typeName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(final String unit) {
        this.unit = unit;
    }

    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Attribute)) {
            return false;
        }
        final Attribute attribute = (Attribute) o;
        return !(name != null ? !name.equals(attribute.name) : attribute.name != null);
    }

    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public FieldSetter[] getSetters() {
        return new FieldSetter[]{
                new FieldSetter(FIELD_TITLE) {
                    public void setValue(final Object value) {
                        title = (String) value;
                    }
                },
                new FieldSetter(FIELD_NAME) {
                    public void setValue(final Object value) {
                        name = (String) value;
                    }
                },
                new FieldSetter(FIELD_TYPE_NAME) {
                    public void setValue(final Object value) {
                        typeName = (String) value;
                    }
                },
                new FieldSetter(FIELD_VALUE) {
                    public void setValue(final Object value) {
                        Attribute.this.value = (String) value;
                    }
                },
                new FieldSetter(FIELD_UNIT) {
                    public void setValue(final Object value) {
                        unit = (String) value;
                    }
                }
        };
    }

    public FieldGetter[] getGetters() {
        return new FieldGetter[]{
                new FieldGetter(FIELD_TITLE) {
                    public Object getValue() {
                        return title;
                    }
                },
                new FieldGetter(FIELD_NAME) {
                    public Object getValue() {
                        return name;
                    }
                },
                new FieldGetter(FIELD_TYPE_NAME) {
                    public Object getValue() {
                        return typeName;
                    }
                },
                new FieldGetter(FIELD_VALUE) {
                    public Object getValue() {
                        return value;
                    }
                },
                new FieldGetter(FIELD_UNIT) {
                    public Object getValue() {
                        return unit;
                    }
                }
        };
    }
}