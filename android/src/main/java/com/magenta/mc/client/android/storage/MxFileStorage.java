package com.magenta.mc.client.android.storage;

import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.exception.StorageException;
import com.magenta.mc.client.android.storage.file.FileStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MxFileStorage extends FileStorage {

    public MxFileStorage(final File storageFolder) {
        super(storageFolder);
    }

    private File getDir(StorableMetadata metadata) {
        final File dir;
        if (metadata.common) {
            dir = new File(storageFolder, metadata.name);
        } else {
            String userId = Settings.get().getUserId();
            dir = new File(new File(storageFolder, userId), metadata.name);
        }
        if (!dir.exists()) {
            try {
                metadata.storageLock.writeLock().acquire();
                if (!dir.exists() && !dir.mkdirs()) {
                    throw new StorageException("Cannot create storage dir: " + dir.getPath());
                }
            } catch (InterruptedException e) {
                throw new StorageException("Storage.getDir interrupted: " + dir.getPath());
            } finally {
                metadata.storageLock.writeLock().release();
            }
        }
        return dir;
    }

    public List<Storable> load(StorableMetadata metadata) throws StorageException {
        try {
            metadata.storageLock.readLock().acquire();
            final File dir = getDir(metadata);
            String[] ids = dir.list();
            Arrays.sort(ids, new Comparator() {
                public int compare(Object o1, Object o2) {
                    if (o1 == null) return 1;
                    if (o2 == null) return -1;
                    return ((String) o1).compareTo(((String) o2));
                }
            });
            List<Storable> result = new ArrayList<>(ids.length);
            for (String id : ids) {
                Storable nextStorable = load(metadata, id);
                if (nextStorable != null)
                    result.add(nextStorable);
            }
            return result;
        } catch (InterruptedException e) {
            return new ArrayList<>();
        } finally {
            metadata.storageLock.readLock().release();
        }
    }
}
