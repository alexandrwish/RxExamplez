package com.magenta.mc.client.android.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.magenta.mc.client.android.entity.type.OrderItemType;
import com.magenta.mc.client.android.record.OrderItemRecord;
import com.magenta.mc.client.android.util.StringUtils;

@DatabaseTable(tableName = "order_item")
public class OrderItemEntity extends JobStopEntity<OrderItemRecord> {

    @DatabaseField(columnName = "barcode")
    private String barcode;
    @DatabaseField(columnName = "name")
    private String name;
    @DatabaseField(columnName = "status", dataType = DataType.ENUM_STRING)
    private OrderItemType status;

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

    public OrderItemType getStatus() {
        return status;
    }

    public void setStatus(OrderItemType status) {
        this.status = status;
    }

    public OrderItemRecord toRecord() {
        return new OrderItemRecord(Long.valueOf(getMxID()), getBarcode(), getName(), status);
    }
}