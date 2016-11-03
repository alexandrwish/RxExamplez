package com.magenta.maxunits.mobile.dlib.db.dao;

import android.content.Context;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.magenta.maxunits.mobile.MxApplication;
import com.magenta.maxunits.mobile.dlib.db.CacheDBHelper;
import com.magenta.maxunits.mobile.dlib.db.DBAdapter;
import com.magenta.maxunits.mobile.dlib.entity.TileCacheEntity;

import java.sql.SQLException;
import java.util.List;

public class TileCacheDAO {

    protected static TileCacheDAO instance;
    protected final Context context;
    protected final DBAdapter adapter;
    protected Dao<TileCacheEntity, Integer> dao;

    private TileCacheDAO(Context context) {
        this.context = context;
        this.adapter = (DBAdapter) MxApplication.getInstance().getDBAdapter();
    }

    public static TileCacheDAO getInstance(Context context) {
        if (instance == null) {
            instance = new TileCacheDAO(context);
        }
        return instance;
    }

    public Dao<TileCacheEntity, Integer> getDaoStore() throws SQLException {
        if (dao == null) {
            dao = DaoManager.createDao(new AndroidConnectionSource(adapter.getDB(CacheDBHelper.DATABASE_NAME)), TileCacheEntity.class);
        }
        return dao;
    }

    public List<TileCacheEntity> getTileFromCache(String name, int x, int y, int z) throws SQLException {
        return getDaoStore().queryBuilder().where().like("provider", name).and().like("x", x).and().like("y", y).and().like("z", z).query();
    }

    public void saveTileToCache(TileCacheEntity entity) throws SQLException {
        getDaoStore().create(entity);
    }

    public void removeCacheTiles(Long maxAge) throws SQLException {
        Dao<TileCacheEntity, Integer> dao = getDaoStore();
        if (maxAge != null) {
            DeleteBuilder<TileCacheEntity, Integer> builder = dao.deleteBuilder();
            builder.where().le("last_access_date", maxAge);
            dao.delete(builder.prepare());
        } else {
            long count = dao.countOf();
            List<TileCacheEntity> entities = dao.queryBuilder().orderBy("last_access_date", false).limit(count / 10).query();
            if (entities.isEmpty()) return;
            dao.delete(entities);
        }
    }

    public void updateUsedDate(TileCacheEntity entity) throws SQLException {
        Dao<TileCacheEntity, Integer> dao = getDaoStore();
        entity.setLastAccessDate(System.currentTimeMillis());
        dao.update(entity);
    }
}