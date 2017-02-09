package com.magenta.maxunits.mobile.dlib.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.magenta.maxunits.mobile.dlib.MxApplication;
import com.magenta.maxunits.mobile.dlib.db.MxDBOpenHelper;

public abstract class AbstractDAO {

    private final static String SQL_COUNT = "SELECT count(*) FROM %s";
    private final static String SQL_EXISTS = "SELECT count(*) FROM %s WHERE %s";

    protected final Context context;
    protected final MxDBOpenHelper dbOpenHelper;
    protected SQLiteDatabase db;

    public AbstractDAO(Context context) {
        this.context = context;
        this.dbOpenHelper = (MxDBOpenHelper) MxApplication.getInstance().getDBAdapter().getHelper(MxDBOpenHelper.DATABASE_NAME);
    }

    protected static Cursor query(final SQLiteDatabase db, final String table, final String[] columns, final String selection, final String[] selectionArgs) {
        final SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(table);
        final Cursor cursor = builder.query(db, columns, selection, selectionArgs, null, null, null);
        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    protected static int count(final SQLiteDatabase db, final String table) {
        final Cursor cursor = db.rawQuery(String.format(SQL_COUNT, table), null);
        if (cursor == null) {
            return 0;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return 0;
        }
        try {
            return cursor.getInt(0);
        } finally {
            cursor.close();
        }
    }

    protected static boolean exists(final SQLiteDatabase db, final String table, final String selection, final String[] selectionArgs) {
        final Cursor cursor = db.rawQuery(String.format(SQL_EXISTS, table, selection), selectionArgs);
        if (cursor == null) {
            return false;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }
        try {
            return cursor.getInt(0) > 0;
        } finally {
            cursor.close();
        }
    }

    protected static String where(final String... columns) {
        final StringBuilder sb = new StringBuilder();
        for (final String column : columns) {
            sb.append(sb.length() > 0 ? " and " : "").append(column).append("=?");
        }
        return sb.toString();
    }

    public void open() throws SQLException {
        open(false);
    }

    public SQLiteDatabase open(final boolean writable) throws SQLException {
        close();
        return db = writable ? dbOpenHelper.getWritableDatabase() : dbOpenHelper.getReadableDatabase();
    }

    public void close() {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    public void free() {
        close();
        dbOpenHelper.close();
    }

    @SuppressWarnings("unchecked")
    public <DAO, R> R execute(final Sandbox<DAO, R> sandbox, final boolean writable) {
        open(writable);
        final boolean isInTransaction = sandbox.isInTransaction();
        try {
            if (isInTransaction) {
                db.beginTransaction();
            }
            final R result = sandbox.run((DAO) this, db);
            if (isInTransaction) {
                db.setTransactionSuccessful();
            }
            return result;
        } finally {
            if (isInTransaction) {
                db.endTransaction();
            }
            close();
        }
    }

    @SuppressWarnings("unchecked")
    public <DAO, R> R execute(final Sandbox<DAO, R> sandbox) {
        return execute(sandbox, false);
    }

    public static abstract class Sandbox<DAO, R> {

        protected boolean inTransaction;

        public abstract R run(DAO dao, SQLiteDatabase db);

        public Sandbox<DAO, R> withTransaction(final boolean inTransaction) {
            this.inTransaction = inTransaction;
            return this;
        }

        public boolean isInTransaction() {
            return inTransaction;
        }
    }

    public static abstract class SandboxNoResult<DAO> extends Sandbox<DAO, Void> {

        public Void run(final DAO dao, final SQLiteDatabase db) {
            runNoResult(dao, db);
            return null;
        }

        public abstract void runNoResult(DAO dao, SQLiteDatabase db);
    }
}