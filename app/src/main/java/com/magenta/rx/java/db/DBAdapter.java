package com.magenta.rx.java.db;

import android.content.Context;
import android.content.SharedPreferences;

import com.magenta.rx.java.RXApplication;
import com.magenta.rx.java.model.entity.DaoMaster;
import com.magenta.rx.java.model.entity.DaoSession;

import javax.inject.Inject;

public class DBAdapter {

    @Inject
    SharedPreferences preferences;
    private DaoSession session;

    public DBAdapter(Context context) {
        RXApplication.getInstance().inject(this);
        init(context);
    }

    private void init(Context context) {
        DBHelper helper = new DBHelper(context, preferences.getString("db_name", ""), Integer.valueOf(preferences.getString("db_version", "0")));
        session = new DaoMaster(helper.getWritableDatabase()).newSession();
    }

    public DaoSession getMainSession() {
        return session;
    }
}