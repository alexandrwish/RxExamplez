package com.magenta.rx.java.db;

import android.content.Context;
import android.content.SharedPreferences;

import com.magenta.rx.java.RXApplication;
import com.magenta.rx.java.model.entity.DaoMaster;
import com.magenta.rx.java.model.entity.DaoSession;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseOpenHelper;

import javax.inject.Inject;

public class DBAdapter {

    private final DaoSession session;

    @Inject
    public DBAdapter(SharedPreferences preferences) {
        DBHelper helper = new DBHelper(RXApplication.getInstance(), preferences.getString("db_name", ""), Integer.valueOf(preferences.getString("db_version", "0")));
        session = new DaoMaster(helper.getWritableDatabase()).newSession();
    }

    public DaoSession getMainSession() {
        return session;
    }

    private static class DBHelper extends DatabaseOpenHelper {

        DBHelper(Context context, String name, int version) {
            super(context, name, version);
        }

        public void onCreate(Database db) {
            DaoMaster.createAllTables(db, false);
        }

        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            DaoMaster.dropAllTables(db, true);
            onCreate(db);
        }
    }
}