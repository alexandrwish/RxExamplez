package com.magenta.mc.client.storage;

import com.magenta.mc.client.setup.Setup;

/**
 * Created by IntelliJ IDEA.
 * User: const
 * Date: 09.06.12
 * Time: 19:38
 * To change this template use File | Settings | File Templates.
 */
public class LazyStorable extends Storable {

    private static final long serialVersionUID = -2822587752202804192L;
    boolean loaded;
    private StorableMetadata metadata;
    private String id;
    private Storable data;

    public LazyStorable(StorableMetadata metadata, String id) {
        this.metadata = metadata;
        this.id = id;
    }

    public Storable getData() {
        load();
        return data;
    }

    public void unload() {
        loaded = false;
        data = null;
    }

    public void save() {
        Setup.get().getStorage().save(getData());
    }

    private void load() {
        if (!loaded) {
            data = Setup.get().getStorage().load(metadata, id);
            loaded = true;
        }
    }

    public StorableMetadata getMetadata() {
        return metadata;
    }

    public String getId() {
        return id;
    }

    public FieldSetter[] getSetters() {
        throw new UnsupportedOperationException("getSetters not supported in LazyStorable");
    }

    public FieldGetter[] getGetters() {
        throw new UnsupportedOperationException("getGetters not supported in LazyStorable");
    }
}
