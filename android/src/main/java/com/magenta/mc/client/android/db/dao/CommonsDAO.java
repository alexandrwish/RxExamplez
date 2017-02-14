package com.magenta.mc.client.android.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class CommonsDAO extends AbstractDAO {

    public CommonsDAO(final Context context) {
        super(context);
    }

    public void removeAllJobData(final String jobId) {
        execute(new SandboxNoResult<CommonsDAO>() {
            public void runNoResult(final CommonsDAO dao, final SQLiteDatabase db) {
                db.delete(StopsDAO.TABLE, where(StopsDAO.COLUMN_JOB_ID), new String[]{jobId});
                db.delete(SignatureDAO.TABLE, where(SignatureDAO.COLUMN_JOB_ID), new String[]{jobId});
                db.delete(BarcodesDAO.TABLE, where(BarcodesDAO.COLUMN_JOB_ID), new String[]{jobId});
            }
        }.withTransaction(true), true);
    }

    public void updateJobReferens(final String oldReference, final String newReference) {
        final ContentValues values = new ContentValues();
        values.put(StopsDAO.COLUMN_JOB_ID, newReference);
        execute(new SandboxNoResult<CommonsDAO>() {
            public void runNoResult(CommonsDAO dao, SQLiteDatabase db) {
                db.update(StopsDAO.TABLE, values, where(StopsDAO.COLUMN_JOB_ID), new String[]{oldReference});
                db.update(SignatureDAO.TABLE, values, where(SignatureDAO.COLUMN_JOB_ID), new String[]{oldReference});
                db.update(BarcodesDAO.TABLE, values, where(BarcodesDAO.COLUMN_JOB_ID), new String[]{oldReference});
            }
        }.withTransaction(true), true);
    }
}