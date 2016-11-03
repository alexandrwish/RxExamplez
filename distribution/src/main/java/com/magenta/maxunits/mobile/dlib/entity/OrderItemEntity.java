package com.magenta.maxunits.mobile.dlib.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.magenta.maxunits.mobile.dlib.record.OrderItemRecord;
import com.magenta.maxunits.mobile.utils.StringUtils;

@DatabaseTable(tableName = "order_item")
public class OrderItemEntity extends JobStopEntity<OrderItemRecord> {

    @DatabaseField(columnName = "barcode")
    private String barcode;
    @DatabaseField(columnName = "name")
    private String name;
    @DatabaseField(columnName = "status", dataType = DataType.ENUM_STRING)
    private OrderItemStatus status;

    public String getBarcode() {
        return StringUtils.decodeURI(barcode);
    }

    public void setBarcode(String barcode) {
        this.barcode = StringUtils.encodeURI(barcode);
    }

    public String getName() {
        return StringUtils.decodeURI(name);
    }

    public void setName(String name) {
        this.name = StringUtils.encodeURI(name);
    }

    public OrderItemStatus getStatus() {
        return status;
    }

    public void setStatus(OrderItemStatus status) {
        this.status = status;
    }

    public OrderItemRecord toRecord() {
        return new OrderItemRecord(Long.valueOf(getMxID()), getBarcode(), getName(), status);
    }
}