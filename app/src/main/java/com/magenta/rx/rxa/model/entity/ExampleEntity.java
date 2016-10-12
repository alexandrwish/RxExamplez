package com.magenta.rx.rxa.model.entity;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.List;

@Entity(nameInDb = "example")
public class ExampleEntity implements TextedEntity {

    @Id(autoincrement = true)
    private Long id;
    private Long trId;
    @ToOne(joinProperty = "trId")
    private TranscriptionEntity transcriptionEntity;
    private String text;
    @ToMany(referencedJoinProperty = "exId")
    private List<TranscriptionEntity> tr;

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 353710926)
    public synchronized void resetTr() {
        tr = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 446040656)
    public List<TranscriptionEntity> getTr() {
        if (tr == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TranscriptionEntityDao targetDao = daoSession.getTranscriptionEntityDao();
            List<TranscriptionEntity> trNew = targetDao._queryExampleEntity_Tr(id);
            synchronized (this) {
                if (tr == null) {
                    tr = trNew;
                }
            }
        }
        return tr;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1745882737)
    public void setTranscriptionEntity(TranscriptionEntity transcriptionEntity) {
        synchronized (this) {
            this.transcriptionEntity = transcriptionEntity;
            trId = transcriptionEntity == null ? null : transcriptionEntity.getId();
            transcriptionEntity__resolvedKey = trId;
        }
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 719994707)
    public TranscriptionEntity getTranscriptionEntity() {
        Long __key = this.trId;
        if (transcriptionEntity__resolvedKey == null
                || !transcriptionEntity__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TranscriptionEntityDao targetDao = daoSession
                    .getTranscriptionEntityDao();
            TranscriptionEntity transcriptionEntityNew = targetDao.load(__key);
            synchronized (this) {
                transcriptionEntity = transcriptionEntityNew;
                transcriptionEntity__resolvedKey = __key;
            }
        }
        return transcriptionEntity;
    }

    @Generated(hash = 1162801843)
    private transient Long transcriptionEntity__resolvedKey;

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 551808277)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getExampleEntityDao() : null;
    }

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 3071140)
    private transient ExampleEntityDao myDao;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getTrId() {
        return this.trId;
    }

    public void setTrId(Long trId) {
        this.trId = trId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 599531057)
    public ExampleEntity(Long id, Long trId, String text) {
        this.id = id;
        this.trId = trId;
        this.text = text;
    }

    @Generated(hash = 432642163)
    public ExampleEntity() {
    }
}