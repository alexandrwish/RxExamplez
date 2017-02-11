package com.magenta.mc.client.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.magenta.mc.client.android.entity.TileCacheEntity;

public class CacheDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "cache_db";

    private static final int DATABASE_VERSION = 1;
    private static final Class[] TABLES = new Class[]{
            TileCacheEntity.class
    };

    public CacheDBHelper(Context context) {
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
            Log.e(getClass().getName(), ignore.getMessage(), ignore);
        }
        createTables(source);
    }

    private void createTables(ConnectionSource source) {
        try {
            for (Class table : TABLES) {
                TableUtils.createTable(source, table);
            }
        } catch (Exception ignore) {
            Log.e(getClass().getName(), ignore.getMessage(), ignore);
        }
    }
}