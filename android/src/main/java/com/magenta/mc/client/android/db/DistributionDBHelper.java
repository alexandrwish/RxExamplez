package com.magenta.mc.client.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.magenta.mc.client.android.entity.DynamicAttributeEntity;
import com.magenta.mc.client.android.entity.LocalizeStringEntity;
import com.magenta.mc.client.android.entity.LocationEntity;
import com.magenta.mc.client.android.entity.MapSettingsEntity;
import com.magenta.mc.client.android.entity.OrderItemEntity;
import com.magenta.mc.client.android.log.MCLoggerFactory;

public class DistributionDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "distribution_db";

    private static final int DATABASE_VERSION = 3;

    private static final Class[] TABLES = new Class[]{
            MapSettingsEntity.class,
            OrderItemEntity.class,
            DynamicAttributeEntity.class,
            LocationEntity.class,
            LocalizeStringEntity.class
    };

    public DistributionDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTables(new AndroidConnectionSource(sqLiteDatabase));
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        ConnectionSource source = new AndroidConnectionSource(sqLiteDatabase);
        try {
            for (Class table : TABLES) {
                TableUtils.dropTable(source, table, true);
            }
        } catch (Exception ignore) {
        }
        createTables(source);
    }

    private void createTables(ConnectionSource source) {
        MCLoggerFactory.getLogger(getClass()).info("DB init!");
        try {
            for (Class table : TABLES) {
                TableUtils.createTable(source, table);
            }
        } catch (Exception ignore) {
        }
    }
}