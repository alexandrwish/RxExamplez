package com.magenta.mc.client.android.entity;

import com.j256.ormlite.field.DatabaseField;

public abstract class AbstractEntity<T> {

    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    protected int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public abstract T toRecord();
}