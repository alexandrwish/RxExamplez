package com.magenta.maxunits.mobile.dlib.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.magenta.maxunits.mobile.dlib.db.dao.BarcodesDAO;
import com.magenta.maxunits.mobile.dlib.db.dao.SignatureDAO;
import com.magenta.maxunits.mobile.dlib.db.dao.StopsDAO;

import static java.lang.String.format;

public class MxDBOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final TableMeta[] TABLES = new TableMeta[]{
            new TableMeta(StopsDAO.TABLE, StopsDAO.SQL_CREATE_TABLE, StopsDAO.INDEX),
            new TableMeta(BarcodesDAO.TABLE, BarcodesDAO.SQL_CREATE_TABLE, BarcodesDAO.INDEX),
            new TableMeta(SignatureDAO.TABLE, SignatureDAO.SQL_CREATE_TABLE, SignatureDAO.INDEX)
    };
    public static String DATABASE_NAME = "maxunits";

    public MxDBOpenHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static void setDatabase(final String dbName) {
        DATABASE_NAME = dbName;
    }

    public void onCreate(final SQLiteDatabase db) {
        for (final TableMeta meta : TABLES) {
            db.execSQL(meta.sqlCreateTable);
            if (meta.index != null && meta.index.length > 0) {
                for (final String index : meta.index) {
                    db.execSQL(format("CREATE INDEX %s_%s_idx ON %s (%s);", meta.table, index, meta.table, index));
                }
            }
        }
    }

    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        for (final TableMeta meta : TABLES) {
            db.execSQL(String.format("DROP TABLE IF EXISTS %s", meta.table));
            if (meta.index != null && meta.index.length > 0) {
                for (final String index : meta.index) {
                    db.execSQL(format("DROP INDEX IF EXISTS %s_%s_idx;", meta.table, index));
                }
            }
        }
        onCreate(db);
    }

    private static final class TableMeta {
        public final String table;
        public final String sqlCreateTable;
        public final String[] index;

        public TableMeta(final String table, final String sqlCreateTable, final String[] index) {
            this.table = table;
            this.sqlCreateTable = sqlCreateTable;
            this.index = index;
        }
    }
}