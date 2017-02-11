package com.magenta.mc.client.android.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static java.lang.String.format;

public class StopsDAO extends AbstractDAO {

    public static final String TABLE = "stops";

    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_JOB_ID = "job_id";
    public static final String COLUMN_STOP_ID = "stop_id";
    public static final String COLUMN_STATE = "state";

    public static final String[] INDEX = new String[]{COLUMN_JOB_ID, COLUMN_STOP_ID};

    public static final String SQL_CREATE_TABLE = format(
            "CREATE TABLE %s (id INTEGER PRIMARY KEY, %s INTEGER, %s TEXT, %s  TEXT, %s INTEGER)",
            TABLE, COLUMN_DATE, COLUMN_JOB_ID, COLUMN_STOP_ID, COLUMN_STATE);

    public StopsDAO(final Context context) {
        super(context);
    }

    private static void updateOrCreateRow(final SQLiteDatabase db, final String jobId, final String stopId, final ContentValues contentValues) {
        final String where = where(COLUMN_JOB_ID, COLUMN_STOP_ID);
        final String[] whereArg = new String[]{jobId, stopId};
        if (exists(db, TABLE, where, whereArg)) {
            db.update(TABLE, contentValues, where, whereArg);
        } else {
            contentValues.put(COLUMN_DATE, System.currentTimeMillis());
            contentValues.put(COLUMN_JOB_ID, jobId);
            contentValues.put(COLUMN_STOP_ID, stopId);
            db.insert(TABLE, null, contentValues);
        }
    }

    public int getState(final String jobId, final String stopId) {
        return execute(new Sandbox<StopsDAO, Integer>() {
            public Integer run(final StopsDAO dao, final SQLiteDatabase db) {
                final Cursor cursor = query(db, TABLE, new String[]{COLUMN_STATE},
                        where(COLUMN_JOB_ID, COLUMN_STOP_ID), new String[]{jobId, stopId});
                return cursor != null ? cursor.getInt(0) : -1;
            }
        });
    }

    public void updateState(final String jobId, final String stopId, final int state) {
        execute(new SandboxNoResult<StopsDAO>() {
            public void runNoResult(final StopsDAO dao, final SQLiteDatabase db) {
                final ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_STATE, state);
                updateOrCreateRow(db, jobId, stopId, contentValues);
            }
        }.withTransaction(true), true);
    }
}