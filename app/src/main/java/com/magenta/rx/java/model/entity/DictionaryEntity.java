package com.magenta.rx.java.model.entity;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

@Entity(nameInDb = "dictionary")
public class DictionaryEntity {

    @Id
    private String word;
    @ToMany(referencedJoinProperty = "wordId")
    private List<DefinitionEntity> def;

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
    @Generated(hash = 1464771354)
    public synchronized void resetDef() {
        def = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 13240866)
    public List<DefinitionEntity> getDef() {
        if (def == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DefinitionEntityDao targetDao = daoSession.getDefinitionEntityDao();
            List<DefinitionEntity> defNew = targetDao._queryDictionaryEntity_Def(word);
            synchronized (this) {
                if (def == null) {
                    def = defNew;
                }
            }
        }
        return def;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 419801646)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDictionaryEntityDao() : null;
    }

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1846642217)
    private transient DictionaryEntityDao myDao;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    public String getWord() {
        return this.word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Generated(hash = 1058442812)
    public DictionaryEntity(String word) {
        this.word = word;
    }

    @Generated(hash = 1575884422)
    public DictionaryEntity() {
    }
}