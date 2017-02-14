package com.magenta.mc.client.android.mc.storage;

import java.io.Serializable;

import EDU.oswego.cs.dl.util.concurrent.ReentrantWriterPreferenceReadWriteLock;

/**
 * @author Petr Popov
 *         Created: 24.01.12 9:35
 */
public class StorableMetadata implements Serializable {

    public final String name;
    public final boolean common;

    public final ReentrantWriterPreferenceReadWriteLock storageLock = new ReentrantWriterPreferenceReadWriteLock();

    public StorableMetadata(String name, boolean common) {
        this.name = name;
        this.common = common;
    }

    public StorableMetadata(String name) {
        this(name, false);
    }

    public boolean equals(Object other) {
        return other instanceof StorableMetadata
                && name.equals(((StorableMetadata) other).name)
                && common == ((StorableMetadata) other).common;
    }

    public int hashCode() {
        //return ((common) ? 1 : 0) + 3 * ((consecutive) ? 1 : 0) + 5 * name.hashCode();
        return ((common) ? 1 : 0) + 3 * name.hashCode();
    }

}
