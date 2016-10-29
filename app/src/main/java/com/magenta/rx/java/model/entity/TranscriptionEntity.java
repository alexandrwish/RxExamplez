package com.magenta.rx.java.model.entity;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.List;

@Entity(nameInDb = "transcription")
public class TranscriptionEntity {

    @Id(autoincrement = true)
    private Long id;
    private Long defId;
    @ToOne(joinProperty = "defId")
    private DefinitionEntity def;
    private String text;
    private String pos;
    @ToMany(referencedJoinProperty = "trId")
    private List<SynonymEntity> syn;
    @ToMany(referencedJoinProperty = "trId")
    private List<MeaningEntity> mean;
    @ToMany(referencedJoinProperty = "trId")
    private List<ExampleEntity> ex;
    private Long exId;
    @ToOne(joinProperty = "exId")
    private ExampleEntity example;

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
    @Generated(hash = 1012812513)
    public synchronized void resetEx() {
        ex = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1173350971)
    public List<ExampleEntity> getEx() {
        if (ex == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ExampleEntityDao targetDao = daoSession.getExampleEntityDao();
            List<ExampleEntity> exNew = targetDao._queryTranscriptionEntity_Ex(id);
            synchronized (this) {
                if (ex == null) {
                    ex = exNew;
                }
            }
        }
        return ex;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 2098208286)
    public synchronized void resetMean() {
        mean = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 889622201)
    public List<MeaningEntity> getMean() {
        if (mean == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MeaningEntityDao targetDao = daoSession.getMeaningEntityDao();
            List<MeaningEntity> meanNew = targetDao._queryTranscriptionEntity_Mean(id);
            synchronized (this) {
                if (mean == null) {
                    mean = meanNew;
                }
            }
        }
        return mean;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1163924033)
    public synchronized void resetSyn() {
        syn = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 669397351)
    public List<SynonymEntity> getSyn() {
        if (syn == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SynonymEntityDao targetDao = daoSession.getSynonymEntityDao();
            List<SynonymEntity> synNew = targetDao._queryTranscriptionEntity_Syn(id);
            synchronized (this) {
                if (syn == null) {
                    syn = synNew;
                }
            }
        }
        return syn;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1101635310)
    public void setExample(ExampleEntity example) {
        synchronized (this) {
            this.example = example;
            exId = example == null ? null : example.getId();
            example__resolvedKey = exId;
        }
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 1936638635)
    public ExampleEntity getExample() {
        Long __key = this.exId;
        if (example__resolvedKey == null || !example__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ExampleEntityDao targetDao = daoSession.getExampleEntityDao();
            ExampleEntity exampleNew = targetDao.load(__key);
            synchronized (this) {
                example = exampleNew;
                example__resolvedKey = __key;
            }
        }
        return example;
    }

    @Generated(hash = 1847817199)
    private transient Long example__resolvedKey;

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 796390465)
    public void setDef(DefinitionEntity def) {
        synchronized (this) {
            this.def = def;
            defId = def == null ? null : def.getId();
            def__resolvedKey = defId;
        }
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 685176738)
    public DefinitionEntity getDef() {
        Long __key = this.defId;
        if (def__resolvedKey == null || !def__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DefinitionEntityDao targetDao = daoSession.getDefinitionEntityDao();
            DefinitionEntity defNew = targetDao.load(__key);
            synchronized (this) {
                def = defNew;
                def__resolvedKey = __key;
            }
        }
        return def;
    }

    @Generated(hash = 1723176860)
    private transient Long def__resolvedKey;

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 411875938)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTranscriptionEntityDao() : null;
    }

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1484483872)
    private transient TranscriptionEntityDao myDao;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    public Long getExId() {
        return this.exId;
    }

    public void setExId(Long exId) {
        this.exId = exId;
    }

    public String getPos() {
        return this.pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getDefId() {
        return this.defId;
    }

    public void setDefId(Long defId) {
        this.defId = defId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 1882797814)
    public TranscriptionEntity(Long id, Long defId, String text, String pos,
                               Long exId) {
        this.id = id;
        this.defId = defId;
        this.text = text;
        this.pos = pos;
        this.exId = exId;
    }

    @Generated(hash = 1626555282)
    public TranscriptionEntity() {
    }
}