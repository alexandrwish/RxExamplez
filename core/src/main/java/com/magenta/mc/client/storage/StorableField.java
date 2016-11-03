package com.magenta.mc.client.storage;

/**
 * Class for fields of Storable
 *
 * @author Petr Popov
 *         Created: 13.12.11 20:11
 */
public abstract class StorableField extends Storable {

    private static final long serialVersionUID = 9L;

    public StorableMetadata getMetadata() {
        throw new RuntimeException("Object of this class should be persisted only as a part of another storable");
    }

    public String getId() {
        throw new RuntimeException("Object of this class should be persisted only as a part of another storable");
    }

}
