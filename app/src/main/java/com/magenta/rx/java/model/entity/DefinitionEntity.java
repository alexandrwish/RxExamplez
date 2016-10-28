package com.magenta.rx.java.model.entity;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.List;

@Entity(nameInDb = "definition")
public class DefinitionEntity implements TextedEntity {

    @Id(autoincrement = true)
    private Long id;
    private String wordId;
    @ToOne(joinProperty = "wordId")
    private DictionaryEntity dictionary;
    private String text;
    private String pos;
    private String ts;
    @ToMany(referencedJoinProperty = "defId")
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
    @Generated(hash = 1037115820)
    public List<TranscriptionEntity> getTr() {
        if (tr == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TranscriptionEntityDao targetDao = daoSession.getTranscriptionEntityDao();
            List<TranscriptionEntity> trNew = targetDao._queryDefinitionEntity_Tr(id);
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
    @Generated(hash = 390370359)
    public void setDictionary(DictionaryEntity dictionary) {
        synchronized (this) {
            this.dictionary = dictionary;
            wordId = dictionary == null ? null : dictionary.getWord();
            dictionary__resolvedKey = wordId;
        }
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 1559825576)
    public DictionaryEntity getDictionary() {
        String __key = this.wordId;
        if (dictionary__resolvedKey == null || dictionary__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DictionaryEntityDao targetDao = daoSession.getDictionaryEntityDao();
            DictionaryEntity dictionaryNew = targetDao.load(__key);
            synchronized (this) {
                dictionary = dictionaryNew;
                dictionary__resolvedKey = __key;
            }
        }
        return dictionary;
    }

    @Generated(hash = 1145474616)
    private transient String dictionary__resolvedKey;

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1965154909)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDefinitionEntityDao() : null;
    }

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1306464706)
    private transient DefinitionEntityDao myDao;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    public String getTs() {
        return this.ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
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

    public String getWordId() {
        return this.wordId;
    }

    public void setWordId(String wordId) {
        this.wordId = wordId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 1994154801)
    public DefinitionEntity(Long id, String wordId, String text, String pos, String ts) {
        this.id = id;
        this.wordId = wordId;
        this.text = text;
        this.pos = pos;
        this.ts = ts;
    }

    @Generated(hash = 344093570)
    public DefinitionEntity() {
    }

}