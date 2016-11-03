package com.magenta.maxunits.mobile.dlib.db;

import android.content.Context;

import com.magenta.maxunits.mobile.db.MxDBAdapter;

public class DBAdapter extends MxDBAdapter {

    public DBAdapter(Context context) {
        super(context);
    }

    protected void initHelper(Context context) {
        super.initHelper(context);
        databaseManagers.put(CacheDBHelper.DATABASE_NAME, new CacheDBHelper(context));
        databaseManagers.put(DistributionDBHelper.DATABASE_NAME, new DistributionDBHelper(context));
    }
}