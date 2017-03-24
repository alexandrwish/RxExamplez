package com.magenta.mc.client.android.storage;

/**
 * @autor Petr Popov
 * Created 05.03.12 18:48
 */
public class StorableDescriptor {

    private StorableMetadata metadata;
    private String id;

    public StorableDescriptor() {
    }

    public StorableDescriptor(StorableMetadata metadata, String id) {
        this.metadata = metadata;
        this.id = id;
    }

    public StorableMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(StorableMetadata metadata) {
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int hashCode() {
        return ((id != null) ? id.hashCode() : 1) + 3 * ((metadata != null) ? metadata.hashCode() : 1);
    }

}
