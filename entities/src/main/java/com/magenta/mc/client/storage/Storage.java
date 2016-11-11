package com.magenta.mc.client.storage;

import com.magenta.mc.client.exception.StorageException;

import java.io.File;
import java.util.List;

/**
 * @author Petr Popov
 *         Created: 13.12.11 14:40
 */
public interface Storage {

    /**
     * this method is only for demo storage, don't use it for storables store
     *
     * @return
     */
    File getStorageDir();

    void save(Storable storable) throws StorageException;

    List load(StorableMetadata metadata) throws StorageException;

    Storable load(StorableMetadata storableMetadata, String id) throws StorageException;

    Storable loadFirst(StorableMetadata metadata) throws StorageException;

    Storable loadLast(StorableMetadata metadata) throws StorageException;

    void delete(Storable storable) throws StorageException;

    void delete(StorableMetadata metadata, String id) throws StorageException;

    boolean isStorageCorrupted();

    boolean clearCorruptionFlagIfCorrupted();

    BinaryStorable getBinary(StorableMetadata metadata, String id);

    BinaryStorable getBinary(String uri);

    List getBinaries(StorableMetadata metadata);

    void delete(BinaryStorable storable);

    void touch() throws StorageException;
}
