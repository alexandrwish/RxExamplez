package com.magenta.mc.client.android.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static java.lang.String.format;

public class SignatureDAO extends AbstractDAO {

    public static final String TABLE = "signature";

    public static final String COLUMN_JOB_ID = "job_id";
    public static final String COLUMN_STOP_ID = "stop_id";
    public static final String COLUMN_SIGNATURE = "path";
    public static final String COLUMN_CONTACT_NAME = "contact_name";
    public static final String COLUMN_SIGNATURE_TIME = "signature_time";

    public static final String[] INDEX = new String[]{COLUMN_JOB_ID, COLUMN_STOP_ID};

    public static final String SQL_CREATE_TABLE = format("CREATE TABLE %s (id INTEGER PRIMARY KEY,%s TEXT, %s  TEXT,  %s  TEXT, %s  TEXT, %s INTEGER)", TABLE, COLUMN_JOB_ID, COLUMN_STOP_ID, COLUMN_SIGNATURE, COLUMN_CONTACT_NAME, COLUMN_SIGNATURE_TIME);

    public SignatureDAO(final Context context) {
        super(context);
    }

    private static void updateOrCreateRow(final SQLiteDatabase db, final String jobId, final String stopId, final ContentValues contentValues) {
        final String where = where(COLUMN_JOB_ID, COLUMN_STOP_ID);
        final String[] whereArg = new String[]{jobId, stopId};
        if (exists(db, TABLE, where, whereArg)) {
            db.update(TABLE, contentValues, where, whereArg);
        } else {
            contentValues.put(COLUMN_JOB_ID, jobId);
            contentValues.put(COLUMN_STOP_ID, stopId);
            db.insert(TABLE, null, contentValues);
        }
    }

    public String[] get(final String jobId, final String stopId) {
        return execute(new Sandbox<SignatureDAO, String[]>() {
            public String[] run(final SignatureDAO dao, final SQLiteDatabase db) {
                final Cursor cursor = query(db, TABLE, new String[]{COLUMN_CONTACT_NAME, COLUMN_SIGNATURE, COLUMN_SIGNATURE_TIME},
                        where(COLUMN_JOB_ID, COLUMN_STOP_ID), new String[]{jobId, stopId});
                return cursor != null && cursor.moveToFirst() ? new String[]{cursor.getString(0), cursor.getString(1), cursor.getLong(2) + ""} : null;
            }
        });
    }

    public void update(final String jobId, final String stopId, final String contactName, final String path, final Long timestamp) {
        execute(new SandboxNoResult<SignatureDAO>() {
            public void runNoResult(final SignatureDAO dao, final SQLiteDatabase db) {
                final ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_CONTACT_NAME, contactName);
                contentValues.put(COLUMN_SIGNATURE, path);
                contentValues.put(COLUMN_SIGNATURE_TIME, timestamp);
                updateOrCreateRow(db, jobId, stopId, contentValues);
            }
        }.withTransaction(true), true);
    }
}