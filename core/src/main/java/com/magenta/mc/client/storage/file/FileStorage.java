package com.magenta.mc.client.storage.file;

import com.magenta.mc.client.exception.StorageException;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.storage.BinaryStorable;
import com.magenta.mc.client.storage.Storable;
import com.magenta.mc.client.storage.StorableMetadata;
import com.magenta.mc.client.storage.Storage;
import com.magenta.mc.client.util.FileUtils;
import com.magenta.mc.client.util.StrUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: const
 * Date: 09.06.12
 * Time: 18:56
 * To change this template use File | Settings | File Templates.
 */
public class FileStorage implements Storage {

    private static final File META_FILE = new File(".meta");

    protected File storageFolder;
    private boolean isStorageCorrupted = false;

    public FileStorage(File storageFolder) {
        this.storageFolder = storageFolder;
    }

    public void touch() throws StorageException {
        if (!FileUtils.isFileExists(storageFolder, META_FILE)) {
            FileUtils.createFile(storageFolder, META_FILE);
            throw new StorageException();
        }
    }

    public File getStorageDir() {
        return storageFolder;
    }

    public void save(Storable storable) throws StorageException {
        StorableMetadata metadata = storable.getMetadata();
        File storableDir = getDir(metadata);
        File storableFile = new File(storableDir, storable.getId());
        ObjectOutputStream oos = null;
        try {
            try {
                metadata.storageLock.writeLock().acquire();

                if (!storableFile.exists() && !storableFile.createNewFile()) {
                    throw new StorageException("Cannot create storage file: " + storableFile.getAbsolutePath());
                }
                oos = new ObjectOutputStream(new FileOutputStream(storableFile));
                oos.writeObject(storable);
            } catch (InterruptedException e) {
                // ok
            } finally {
                metadata.storageLock.writeLock().release();
            }
        } catch (IOException e) {
            throw new StorageException("Error while serialize item to storage: " + storable + ", " + storableFile, e);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    // oops
                }
            }
        }
    }

    public List load(StorableMetadata metadata) throws StorageException {
        try {
            metadata.storageLock.readLock().acquire();

            final File dir = getDir(metadata);
            String[] ids = dir.list();
            Arrays.sort(ids, new Comparator() {
                public int compare(Object o1, Object o2) {
                    if (o1 == null) return 1;
                    if (o2 == null) return -1;
                    try {
                        long l1 = Long.parseLong((String) o1);
                        long l2 = Long.parseLong((String) o2);
                        if (l1 > l2) {
                            return 1;
                        } else if (l1 < l2) {
                            return -1;
                        } else {
                            return 0;
                        }
                    } catch (NumberFormatException e) {
                        //not numbers, compare as strings (for binary transmissions)
                    }
                    return ((String) o1).compareTo((String) o2);
                }
            });
            List result = new ArrayList(ids.length);
            for (int i = 0; i < ids.length; i++) {
                Storable nextStorable = load(metadata, ids[i]);
                if (nextStorable != null)
                    result.add(nextStorable);
            }
            return result;
        } catch (InterruptedException e) {
            return new ArrayList();
        } finally {
            metadata.storageLock.readLock().release();
        }
    }

    private File getStorableFile(StorableMetadata metadata, String id) {
        final File dir = getDir(metadata);
        File file = new File(dir, id);
        if (!file.exists()) {
            return null;
        } else {
            return file;
        }
    }

    private Storable readSingleStorable(StorableMetadata metadata, String id, File file) throws IOException {
        if (file == null) {
            file = getStorableFile(metadata, id);
            if (file == null) {
                return null;
            }
        }
        ObjectInputStream inputStream = null;
        try {
            try {
                metadata.storageLock.readLock().acquire();

                inputStream = new ObjectInputStream(new FileInputStream(file));
                final Object item = inputStream.readObject();
                return (Storable) item;
            } catch (InterruptedException e) {
                return null;
            } catch (ClassNotFoundException e) {
                throw new IOException("Error loading storable class", e);
            } finally {
                metadata.storageLock.readLock().release();
            }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // oh
                }
            }
        }
    }

    public Storable load(StorableMetadata metadata, String id) throws StorageException {
        try {
            return readSingleStorable(metadata, id, null);
        } catch (IOException e) {
            handleReadingException(metadata, id, e);
        }
        return null;
    }

    private boolean handleReadingException(StorableMetadata metadata, String storableId, IOException readingException) {
        if (readingException != null) {
            MCLoggerFactory.getLogger(FileStorage.class).error("Error while reading " + storableId, readingException);
            boolean deleted = false;
            try {
                delete(metadata, storableId);
            } catch (StorageException e) {
                e.printStackTrace();
            }
            isStorageCorrupted = true;
            if (deleted) {
                MCLoggerFactory.getLogger(FileStorage.class).debug(storableId + " has been deleted");
                return true;
            } else {
                MCLoggerFactory.getLogger(FileStorage.class).error("Cannot delete " + storableId);
                return false;
            }
        } else {
            return true;
        }
    }

    private File[] getSortedFiles(StorableMetadata metadata) {
        final File dir = getDir(metadata);
        File[] files = new File[0];
        try {
            metadata.storageLock.readLock().acquire();
            files = dir.listFiles();
        } catch (InterruptedException e) {
            // ok
        } finally {
            metadata.storageLock.readLock().release();
        }
        if (files.length > 1) {
            Arrays.sort(files, new Comparator() {
                public int compare(Object o1, Object o2) {
                    try {
                        long name1 = Long.parseLong(((File) o1).getName());
                        long name2 = Long.parseLong(((File) o2).getName());
                        return name1 < name2 ? -1 : 1;
                    } catch (Exception e) {
                        return ((File) o1).getName().compareTo(((File) o2).getName());
                    }
                }
            });
        }
        return files;
    }

    public Storable loadFirst(StorableMetadata metadata) throws StorageException {
        File[] files = getSortedFiles(metadata);
        // cycle through files until first successful reading
        int unseccussfulDeleteCount = 0;
        for (int i = 0; i < files.length; i++) {
            File nextFirstFile = files[unseccussfulDeleteCount];
            String storableId = nextFirstFile.getName();
            try {
                return readSingleStorable(metadata, storableId, null);
            } catch (IOException e) {
                if (!handleReadingException(metadata, storableId, e)) {
                    unseccussfulDeleteCount++;
                }
            }
        }
        return null;
    }

    public Storable loadLast(StorableMetadata metadata) throws StorageException {
        File[] files = getSortedFiles(metadata);
        // cycle through files until first successful reading
        int unseccussfulDeleteCount = 0;
        for (int i = 0; i < files.length; i++) {
            File nextLastFile = files[files.length - 1 - unseccussfulDeleteCount];
            String storableId = nextLastFile.getName();
            try {
                return readSingleStorable(metadata, storableId, null);
            } catch (IOException e) {
                handleReadingException(metadata, storableId, e);
            }
        }
        return null;
    }

    private File getDir(StorableMetadata metadata) {
        final File dir;
        if (metadata.common) {
            dir = new File(storageFolder, metadata.name);
        } else {
            String userId = Setup.get().getSettings().getUserId();
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

    public void delete(Storable storable) throws StorageException {
        delete(storable.getMetadata(), storable.getId());
    }

    public void delete(StorableMetadata metadata, String id) throws StorageException {
        File storageFile = new File(getDir(metadata), id);
        if (storageFile.exists()) {
            try {
                metadata.storageLock.writeLock().acquire();
                if (storageFile.exists() && !storageFile.delete()) {
                    throw new StorageException("Cannot delete storage file: " + storageFile.getPath());
                }
            } catch (InterruptedException e) {
                throw new StorageException("Storage.delete interrupted: " + storageFile.getPath());
            } finally {
                metadata.storageLock.writeLock().release();
            }
        }
    }

    public boolean isStorageCorrupted() {
        return isStorageCorrupted || !META_FILE.exists();
    }

    public boolean clearCorruptionFlagIfCorrupted() {
        boolean wasCorrupted = isStorageCorrupted();
        createMetaFile();
        return wasCorrupted;
    }

    private boolean createMetaFile() {
        boolean hasMeta = META_FILE.exists();
        if (!hasMeta) {
            IOException exception = null;
            try {
                hasMeta = META_FILE.createNewFile();
            } catch (IOException ex) {
                exception = ex;
            }
            if (hasMeta) {
                MCLoggerFactory.getLogger(getClass()).warn("Can't create meta file " + META_FILE.getAbsolutePath(), exception);
            }
        }
        return hasMeta;
    }

    public BinaryStorable getBinary(StorableMetadata metadata, String id) {
        return new FileBinaryStorable(metadata.name, id, storageFolder);
    }

    public BinaryStorable getBinary(String uri) {
        if (uri == null) {
            throw new NullPointerException("Cannot operate on binary storable with null URI");
        }
        String location = uri;
        String[] parts = StrUtil.split(uri, ":");
        if (parts.length > 0) {
            if (!"blob".equalsIgnoreCase(parts[0])) {
                throw new IllegalArgumentException("Binary URI should posess 'blob' scheme");
            }
            location = parts[1];
        }
        String[] locationParts = StrUtil.split(location, "/");
        if (locationParts.length != 2) {
            throw new IllegalArgumentException("Binary URI must be formatted as 'sorableName/id'");
        }
        String storableName = locationParts[0];
        String storableId = locationParts[1];
        return getBinary(new StorableMetadata(storableName, true), storableId);
    }

    public List getBinaries(StorableMetadata metadata) {
        List result = new ArrayList();
        File[] files = getSortedFiles(metadata);
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            result.add(new FileBinaryStorable(metadata.name, file.getName(), storageFolder));
        }
        return result;
    }

    public void delete(BinaryStorable storable) throws StorageException {
        delete(new StorableMetadata(storable.getStorableName(), true), storable.getId());
    }
}
