package com.magenta.maxunits.mobile.dlib.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.magenta.maxunits.mobile.dlib.record.DynamicAttributeRecord;
import com.magenta.maxunits.mobile.utils.StringUtils;

@DatabaseTable(tableName = "dynamic_attribute")
public class DynamicAttributeEntity extends JobStopEntity<DynamicAttributeRecord> {

    @DatabaseField(columnName = "pda_editable")
    private boolean pdaEditable;
    @DatabaseField(columnName = "pda_required")
    private boolean pdaRequired;
    @DatabaseField(columnName = "value")
    private String value;
    @DatabaseField(columnName = "name")
    private String name;
    @DatabaseField(columnName = "unit")
    private String unit;
    @DatabaseField(columnName = "title_id", foreign = true, foreignAutoRefresh = true)
    private LocalizeStringEntity title;
    @DatabaseField(columnName = "type", dataType = DataType.ENUM_STRING)
    private DynamicAttributeType typeName;

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
        return StringUtils.decodeURI(value);
    }

    public void setValue(String value) {
        this.value = StringUtils.encodeURI(value);
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

    public LocalizeStringEntity getTitle() {
        return title;
    }

    public void setTitle(LocalizeStringEntity title) {
        this.title = title;
    }

    public DynamicAttributeType getTypeName() {
        return typeName;
    }

    public void setTypeName(DynamicAttributeType typeName) {
        this.typeName = typeName;
    }

    @Override
    public DynamicAttributeRecord toRecord() {
        return new DynamicAttributeRecord(Long.valueOf(getMxID()), getValue());
    }
}