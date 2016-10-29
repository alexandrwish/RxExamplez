package com.magenta.rx.java.model.entity;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;

@Entity(nameInDb = "meaning")
public class MeaningEntity {

    @Id(autoincrement = true)
    private Long id;
    private Long trId;
    @ToOne(joinProperty = "trId")
    private TranscriptionEntity transcription;
    private String text;

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
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1217118237)
    public void setTranscription(TranscriptionEntity transcription) {
        synchronized (this) {
            this.transcription = transcription;
            trId = transcription == null ? null : transcription.getId();
            transcription__resolvedKey = trId;
        }
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 276257689)
    public TranscriptionEntity getTranscription() {
        Long __key = this.trId;
        if (transcription__resolvedKey == null
                || !transcription__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TranscriptionEntityDao targetDao = daoSession
                    .getTranscriptionEntityDao();
            TranscriptionEntity transcriptionNew = targetDao.load(__key);
            synchronized (this) {
                transcription = transcriptionNew;
                transcription__resolvedKey = __key;
            }
        }
        return transcription;
    }

    @Generated(hash = 522900459)
    private transient Long transcription__resolvedKey;

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 134603578)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMeaningEntityDao() : null;
    }

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 420762138)
    private transient MeaningEntityDao myDao;
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

    @Generated(hash = 1274072046)
    public MeaningEntity(Long id, Long trId, String text) {
        this.id = id;
        this.trId = trId;
        this.text = text;
    }

    @Generated(hash = 911631639)
    public MeaningEntity() {
    }
}