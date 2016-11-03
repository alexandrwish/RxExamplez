package com.magenta.maxunits.mobile.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

public class MxDBAdapter {

    protected final Map<String, SQLiteOpenHelper> databaseManagers = new HashMap<String, SQLiteOpenHelper>();
    protected final Map<String, SQLiteDatabase> databases = new HashMap<String, SQLiteDatabase>();

    public MxDBAdapter(final Context context) {
        initHelper(context);
        initDB(context);
    }

    protected void initHelper(Context context) {
        databaseManagers.put(MxDBOpenHelper.DATABASE_NAME, new MxDBOpenHelper(context));
    }

    protected void initDB(Context context) {
        for (Map.Entry<String, SQLiteOpenHelper> entry : databaseManagers.entrySet()) {
            databases.put(entry.getKey(), entry.getValue().getWritableDatabase());
        }
    }

    public void close(String name) {
        if (name != null && !name.isEmpty()) {
            SQLiteDatabase db = databases.remove(name);
            if (db != null) {
                db.close();
            }
            SQLiteOpenHelper helper = databaseManagers.remove(name);
            if (helper != null) {
                helper.close();
            }
        } else {
            for (SQLiteDatabase database : databases.values()) {
                if (database.isOpen()) {
                    database.close();
                }
            }
            databases.clear();
            for (SQLiteOpenHelper helper : databaseManagers.values()) {
                helper.close();
            }
            databaseManagers.clear();
        }
    }

    public SQLiteDatabase getDB(String databaseName) {
        return databases.get(databaseName);
    }

    public SQLiteOpenHelper getHelper(String helperName) {
        return databaseManagers.get(helperName);
    }
}