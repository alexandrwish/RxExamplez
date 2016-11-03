package com.magenta.maxunits.mobile.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class BarcodesDAO extends AbstractDAO {

    public static final String TABLE = "barcodes";

    public static final String COLUMN_JOB_ID = "job_id";
    public static final String COLUMN_STOP_ID = "stop_id";
    public static final String COLUMN_BARCODE = "barcode";

    public static final String[] INDEX = new String[]{COLUMN_JOB_ID, COLUMN_STOP_ID};

    public static final String SQL_CREATE_TABLE = format(
            "CREATE TABLE %s (id INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s  TEXT)",
            TABLE, COLUMN_JOB_ID, COLUMN_STOP_ID, COLUMN_BARCODE);

    public BarcodesDAO(final Context context) {
        super(context);
    }

    public List<String> get(final String jobId, final String stopId) {
        return execute(new Sandbox<BarcodesDAO, List<String>>() {
            @Override
            public List<String> run(final BarcodesDAO dao, final SQLiteDatabase db) {
                final String where = where(COLUMN_JOB_ID, COLUMN_STOP_ID);
                final String[] whereArg = new String[]{jobId, stopId};
                final Cursor cursor = query(db, TABLE, new String[]{COLUMN_BARCODE}, where, whereArg);
                final List<String> result = new ArrayList<String>(0);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        result.add(cursor.getString(0));
                    } while (cursor.moveToNext());
                }

                return result;
            }
        });
    }

    public void update(final String jobId, final String stopId, final List<String> barcodes) {
        execute(new SandboxNoResult<BarcodesDAO>() {
            @Override
            public void runNoResult(final BarcodesDAO dao, final SQLiteDatabase db) {
                final String where = where(COLUMN_JOB_ID, COLUMN_STOP_ID);
                final String[] whereArg = new String[]{jobId, stopId};
                db.delete(TABLE, where, whereArg);
                if (barcodes != null && !barcodes.isEmpty()) {
                    for (final String barcode : barcodes) {
                        if (barcode.trim().isEmpty()) continue;
                        final ContentValues contentValues = new ContentValues();
                        contentValues.put(COLUMN_STOP_ID, stopId);
                        contentValues.put(COLUMN_JOB_ID, jobId);
                        contentValues.put(COLUMN_BARCODE, barcode);
                        db.insert(TABLE, null, contentValues);
                    }
                }
            }
        }.withTransaction(true), true);
    }
}
