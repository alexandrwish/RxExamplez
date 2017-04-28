package com.magenta.mc.client.android.record;

import com.magenta.mc.client.android.entity.OrderItemEntity;
import com.magenta.mc.client.android.entity.type.OrderItemType;

import java.io.Serializable;

public class OrderItemRecord implements Serializable {

    private Long id;
    private String barcode;
    private String name;
    private OrderItemType status;

    public OrderItemRecord(Long id, String barcode, String name, OrderItemType status) {
        this.id = id;
        this.barcode = barcode;
        this.name = name;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrderItemType getStatus() {
        return status;
    }

    public void setStatus(OrderItemType status) {
        this.status = status;
    }

    public OrderItemEntity toEntity() {
        OrderItemEntity result = new OrderItemEntity();
        result.setMxID(String.valueOf(id));
        result.setName(name);
        result.setBarcode(barcode);
        result.setStatus(status);
        return result;
    }
}