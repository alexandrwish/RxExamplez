package com.magenta.rx.rxa.db;

import android.content.Context;

import com.magenta.rx.rxa.model.entity.DaoMaster;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseOpenHelper;

public class DBHelper extends DatabaseOpenHelper {

    public DBHelper(Context context, String name, int version) {
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