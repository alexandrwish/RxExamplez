package com.magenta.rx.kotlin.db

import android.content.SharedPreferences
import com.magenta.rx.java.RXApplication
import com.magenta.rx.java.model.entity.DaoMaster
import com.magenta.rx.java.model.entity.DaoSession
import org.greenrobot.greendao.database.Database
import org.greenrobot.greendao.database.DatabaseOpenHelper
import javax.inject.Inject

class DBAdapter @Inject constructor(preferences: SharedPreferences) {

    val mainSession: DaoSession = DaoMaster(DBHelper(preferences).writableDatabase).newSession()

    private class DBHelper internal constructor(preferences: SharedPreferences) : DatabaseOpenHelper(RXApplication.getInstance(), preferences.getString("db_name", ""), preferences.getString("db_version", "0").toInt()) {

        override fun onCreate(db: Database?) {
            DaoMaster.createAllTables(db, false)
        }

        override fun onUpgrade(db: Database?, oldVersion: Int, newVersion: Int) {
            DaoMaster.dropAllTables(db, true)
            onCreate(db)
        }
    }
}