package com.magenta.maxunits.mobile.dlib.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tile_cache")
public class TileCacheEntity extends AbstractEntity {

    @DatabaseField(columnName = "last_access_date", index = true)
    protected Long lastAccessDate;
    @DatabaseField(columnName = "x", indexName = "tile_cache_map_index")
    protected int x;
    @DatabaseField(columnName = "y", indexName = "tile_cache_map_index")
    protected int y;
    @DatabaseField(columnName = "z", indexName = "tile_cache_map_index")
    protected int z;
    @DatabaseField(columnName = "provider", indexName = "tile_cache_map_index")
    protected String provider;
    @DatabaseField(columnName = "cache", dataType = DataType.BYTE_ARRAY)
    protected byte[] blob;

    public Long getLastAccessDate() {
        return lastAccessDate;
    }

    public void setLastAccessDate(Long lastAccessDate) {
        this.lastAccessDate = lastAccessDate;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider != null ? provider : "default";
    }

    public byte[] getBlob() {
        return blob;
    }

    public void setBlob(byte[] blob) {
        this.blob = blob;
    }

    @Override
    public Object toRecord() {
        return null;
    }
}