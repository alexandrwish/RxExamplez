package com.magenta.rx.kotlin.db

import android.content.Context
import android.content.SharedPreferences
import com.magenta.rx.java.RXApplication
import com.magenta.rx.java.model.entity.DaoMaster
import com.magenta.rx.java.model.entity.DaoSession
import org.greenrobot.greendao.database.Database
import org.greenrobot.greendao.database.DatabaseOpenHelper
import javax.inject.Inject

class DBAdapter @Inject constructor(preferences: SharedPreferences) {

    val mainSession: DaoSession = DaoMaster(DBHelper(RXApplication.getInstance(), preferences.getString("db_name", ""), Integer.valueOf(preferences.getString("db_version", "0"))!!).writableDatabase).newSession()

    private class DBHelper internal constructor(context: Context, name: String, version: Int) : DatabaseOpenHelper(context, name, version) {

        override fun onCreate(db: Database?) {
            DaoMaster.createAllTables(db, false)
        }

        override fun onUpgrade(db: Database?, oldVersion: Int, newVersion: Int) {
            DaoMaster.dropAllTables(db, true)
            onCreate(db)
        }
    }
}