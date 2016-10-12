package com.magenta.rx.rxa.db;

import android.content.Context;
import android.content.SharedPreferences;

import com.magenta.rx.rxa.RXApplication;
import com.magenta.rx.rxa.model.entity.DaoMaster;
import com.magenta.rx.rxa.model.entity.DaoSession;

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