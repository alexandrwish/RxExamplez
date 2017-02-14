package com.magenta.mc.client.android.db.dao;

import android.database.Cursor;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.magenta.mc.client.android.DistributionApplication;
import com.magenta.mc.client.android.db.DBAdapter;
import com.magenta.mc.client.android.db.DistributionDBHelper;
import com.magenta.mc.client.android.entity.AbstractJobStatus;
import com.magenta.mc.client.android.entity.DynamicAttributeEntity;
import com.magenta.mc.client.android.entity.LocalizeStringEntity;
import com.magenta.mc.client.android.entity.LocationEntity;
import com.magenta.mc.client.android.entity.MapSettingsEntity;
import com.magenta.mc.client.android.entity.OrderItemEntity;
import com.magenta.mc.client.android.entity.PhoneStatisticEntity;
import com.magenta.mc.client.android.entity.StatusSenderLock;
import com.magenta.mc.client.android.util.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class DistributionDAO {

    private static DistributionDAO instance;
    private DBAdapter adapter;

    private DistributionDAO() {
        this.adapter = (DBAdapter) DistributionApplication.getInstance().getDBAdapter();
    }

    public static DistributionDAO getInstance() {
        if (instance == null) {
            instance = new DistributionDAO();
        }
        return instance;
    }

    public void createOrderItems(final Collection<OrderItemEntity> entities) throws Exception {
        final ConnectionSource source = new AndroidConnectionSource(adapter.getDB(DistributionDBHelper.DATABASE_NAME));
        final Dao<OrderItemEntity, Integer> dao = DaoManager.createDao(source, OrderItemEntity.class);
        dao.callBatchTasks(new Callable<Void>() {
            public Void call() throws Exception {
                for (OrderItemEntity entity : entities) {
                    dao.createOrUpdate(entity);
                }
                return null;
            }
        });
    }

    public void createOrderItem(OrderItemEntity entity) throws SQLException {
        final ConnectionSource source = new AndroidConnectionSource(adapter.getDB(DistributionDBHelper.DATABASE_NAME));
        final Dao<OrderItemEntity, Integer> dao = DaoManager.createDao(source, OrderItemEntity.class);
        dao.create(entity);
    }

    public void updateOrderItem(OrderItemEntity entity) throws Exception {
        final ConnectionSource source = new AndroidConnectionSource(adapter.getDB(DistributionDBHelper.DATABASE_NAME));
        final Dao<OrderItemEntity, Integer> dao = DaoManager.createDao(source, OrderItemEntity.class);
        dao.createOrUpdate(entity);
    }

    public void removeOrderItem(OrderItemEntity item) throws SQLException {
        ConnectionSource source = new AndroidConnectionSource(adapter.getDB(DistributionDBHelper.DATABASE_NAME));
        Dao<OrderItemEntity, Integer> dao = DaoManager.createDao(source, OrderItemEntity.class);
        dao.delete(item);
    }

    public void clearOrderItems(String stopId) throws SQLException {
        ConnectionSource source = new AndroidConnectionSource(adapter.getDB(DistributionDBHelper.DATABASE_NAME));
        Dao<OrderItemEntity, Integer> dao = DaoManager.createDao(source, OrderItemEntity.class);
        DeleteBuilder<OrderItemEntity, Integer> deleteBuilder = dao.deleteBuilder();
        deleteBuilder.setWhere(dao.deleteBuilder().where().eq("stop", stopId));
        dao.delete(deleteBuilder.prepare());
    }

    public List<OrderItemEntity> getOrderItems(String jobID, String stopID) throws SQLException {
        ConnectionSource source = new AndroidConnectionSource(adapter.getDB(DistributionDBHelper.DATABASE_NAME));
        Dao<OrderItemEntity, Integer> dao = DaoManager.createDao(source, OrderItemEntity.class);
        return dao.queryBuilder().where().eq("job", jobID).and().eq("stop", stopID).query();
    }

    public List<DynamicAttributeEntity> getDynamicAttributes(String jobID, String stopID) throws SQLException {
        ConnectionSource source = new AndroidConnectionSource(adapter.getDB(DistributionDBHelper.DATABASE_NAME));
        Dao<DynamicAttributeEntity, Integer> dao = DaoManager.createDao(source, DynamicAttributeEntity.class);
        return dao.queryBuilder().where().eq("job", jobID).and().eq("stop", stopID).query();
    }

    public void createDynamicAttributes(final Collection<DynamicAttributeEntity> entities) throws Exception {
        final ConnectionSource source = new AndroidConnectionSource(adapter.getDB(DistributionDBHelper.DATABASE_NAME));
        final Dao<DynamicAttributeEntity, Integer> dao = DaoManager.createDao(source, DynamicAttributeEntity.class);
        final Dao<LocalizeStringEntity, Integer> additionalDao = DaoManager.createDao(source, LocalizeStringEntity.class);
        dao.callBatchTasks(new Callable<Void>() {
            public Void call() throws Exception {
                for (DynamicAttributeEntity entity : entities) {
                    additionalDao.createOrUpdate(entity.getTitle());
                    dao.createOrUpdate(entity);
                }
                return null;
            }
        });
    }

    public void updateDynamicAttribute(Integer id, String value) throws SQLException {
        if (id == null) return;
        final ConnectionSource source = new AndroidConnectionSource(adapter.getDB(DistributionDBHelper.DATABASE_NAME));
        final Dao<DynamicAttributeEntity, Integer> dao = DaoManager.createDao(source, DynamicAttributeEntity.class);
        UpdateBuilder<DynamicAttributeEntity, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq("id", id);
        updateBuilder.updateColumnValue("value", StringUtils.encodeURI(value));
        updateBuilder.update();
    }

    public void clearDynamicAttribute(String stopId) throws SQLException {
        ConnectionSource source = new AndroidConnectionSource(adapter.getDB(DistributionDBHelper.DATABASE_NAME));
        Dao<DynamicAttributeEntity, Integer> dao = DaoManager.createDao(source, DynamicAttributeEntity.class);
        DeleteBuilder<DynamicAttributeEntity, Integer> deleteBuilder = dao.deleteBuilder();
        deleteBuilder.setWhere(dao.deleteBuilder().where().eq("stop", stopId));
        dao.delete(deleteBuilder.prepare());
    }

    public void createPhoneState(PhoneStatisticEntity entity) throws SQLException {
        ConnectionSource source = new AndroidConnectionSource(adapter.getDB(DistributionDBHelper.DATABASE_NAME));
        Dao<PhoneStatisticEntity, Integer> dao = DaoManager.createDao(source, PhoneStatisticEntity.class);
        dao.create(entity);
    }

    public List<PhoneStatisticEntity> getPhoneStatistics() throws SQLException {
        ConnectionSource source = new AndroidConnectionSource(adapter.getDB(DistributionDBHelper.DATABASE_NAME));
        Dao<PhoneStatisticEntity, Integer> dao = DaoManager.createDao(source, PhoneStatisticEntity.class);
        return dao.queryBuilder().limit(90L).query();
    }

    public void clearStatistics(Date date) throws SQLException {
        ConnectionSource source = new AndroidConnectionSource(adapter.getDB(DistributionDBHelper.DATABASE_NAME));
        Dao<PhoneStatisticEntity, Integer> dao = DaoManager.createDao(source, PhoneStatisticEntity.class);
        DeleteBuilder<PhoneStatisticEntity, Integer> deleteBuilder = dao.deleteBuilder();
        deleteBuilder.setWhere(dao.deleteBuilder().where().le("date", date));
        dao.delete(deleteBuilder.prepare());
    }

    public List<MapSettingsEntity> getMapSettings(String driver) throws SQLException {
        ConnectionSource source = new AndroidConnectionSource(adapter.getDB(DistributionDBHelper.DATABASE_NAME));
        Dao<MapSettingsEntity, Integer> dao = DaoManager.createDao(source, MapSettingsEntity.class);
        return dao.queryBuilder().where().eq("driver", driver).query();
    }

    public void saveMapSettings(MapSettingsEntity settingsEntity) throws SQLException {
        ConnectionSource source = new AndroidConnectionSource(adapter.getDB(DistributionDBHelper.DATABASE_NAME));
        Dao<MapSettingsEntity, Integer> dao = DaoManager.createDao(source, MapSettingsEntity.class);
        dao.createOrUpdate(settingsEntity);
    }

    public void addLock(AbstractJobStatus status) throws SQLException {
        ConnectionSource source = new AndroidConnectionSource(adapter.getDB(DistributionDBHelper.DATABASE_NAME));
        Dao<StatusSenderLock, Integer> dao = DaoManager.createDao(source, StatusSenderLock.class);
        StatusSenderLock lock = new StatusSenderLock();
        lock.setJobStatus(status.getJobStatus());
        lock.setJobReferenceId(status.getJobReferenceId());
        lock.setJobId(status.getId());
        dao.createOrUpdate(lock);
    }

    public void releaseLock(String id, String ref, String status) throws SQLException {
        ConnectionSource source = new AndroidConnectionSource(adapter.getDB(DistributionDBHelper.DATABASE_NAME));
        Dao<StatusSenderLock, Integer> dao = DaoManager.createDao(source, StatusSenderLock.class);
        DeleteBuilder<StatusSenderLock, Integer> deleteBuilder = dao.deleteBuilder();
        deleteBuilder.setWhere(dao.deleteBuilder().where().eq("job_id", id).and().eq("job_ref", ref).and().eq("job_status", status));
        dao.delete(deleteBuilder.prepare());
    }

    public boolean hasLock(AbstractJobStatus status) throws SQLException {
        ConnectionSource source = new AndroidConnectionSource(adapter.getDB(DistributionDBHelper.DATABASE_NAME));
        Dao<StatusSenderLock, Integer> dao = DaoManager.createDao(source, StatusSenderLock.class);
        List<StatusSenderLock> locks = dao.queryBuilder().where().eq("job_id", status.getId()).and().eq("job_ref", status.getJobReferenceId()).and().eq("job_status", status.getJobStatus()).query();
        return locks == null || locks.isEmpty();
    }

    public void saveLocation(LocationEntity entity) throws SQLException {
        ConnectionSource source = new AndroidConnectionSource(adapter.getDB(DistributionDBHelper.DATABASE_NAME));
        Dao<LocationEntity, Integer> dao = DaoManager.createDao(source, LocationEntity.class);
        dao.createOrUpdate(entity);
    }

    public List<LocationEntity> getGeoLocations(String driver) throws SQLException {
        ConnectionSource source = new AndroidConnectionSource(adapter.getDB(DistributionDBHelper.DATABASE_NAME));
        Dao<LocationEntity, Integer> dao = DaoManager.createDao(source, LocationEntity.class);
        QueryBuilder<LocationEntity, Integer> queryBuilder = dao.queryBuilder();
        queryBuilder.setWhere(queryBuilder.where().eq("user_id", driver));
        return queryBuilder.orderBy("id", true).limit(500L).query();
    }

    public void clearLocations(long id, String driver) throws SQLException {
        ConnectionSource source = new AndroidConnectionSource(adapter.getDB(DistributionDBHelper.DATABASE_NAME));
        Dao<LocationEntity, Integer> dao = DaoManager.createDao(source, LocationEntity.class);
        DeleteBuilder<LocationEntity, Integer> deleteBuilder = dao.deleteBuilder();
        deleteBuilder.setWhere(dao.deleteBuilder().where().le("id", new SelectArg(id)).and().eq("user_id", driver));
        dao.delete(deleteBuilder.prepare());
    }

    public void clearLocationsAfter(long validDate, String driver) throws SQLException {
        ConnectionSource source = new AndroidConnectionSource(adapter.getDB(DistributionDBHelper.DATABASE_NAME));
        Dao<LocationEntity, Integer> dao = DaoManager.createDao(source, LocationEntity.class);
        DeleteBuilder<LocationEntity, Integer> deleteBuilder = dao.deleteBuilder();
        deleteBuilder.setWhere(dao.deleteBuilder().where().ge("date", new SelectArg(validDate)).and().eq("user_id", driver));
        dao.delete(deleteBuilder.prepare());
    }

    public List<String> getDrivers() {
        List<String> clients = new ArrayList<>();
        Cursor cursor = adapter.getDB(DistributionDBHelper.DATABASE_NAME).rawQuery("select distinct loc.user_id from geo_locations loc", new String[]{});
        try {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                clients.add(cursor.getString(0));
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return clients;
    }
}