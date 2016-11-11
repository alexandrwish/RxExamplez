package com.magenta.mc.client.util;

import com.magenta.mc.client.log.MCLoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Project: mobile-central
 * Author:  Alexey Osipov
 * Created: 22.11.13
 * <p/>
 * Copyright (c) 1999-2013 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 */
public class FileUtils {

    public static boolean isFileExists(File parentDir, File targetFile) {
        if (parentDir == null || targetFile == null) {
            throw new NullPointerException();
        }
        if (!parentDir.exists()) return false;
        if (!parentDir.isDirectory()) throw new IllegalArgumentException();

        File[] children = parentDir.listFiles();
        if (children == null) {
            return false;
        }

        File absoluteTargetFile = new File(parentDir, targetFile.getName());
        for (int i = 0; i < children.length; i++) {
            File child = children[i];
            if (child.equals(absoluteTargetFile)) {
                return true;
            }
        }
        return false;
    }

    public static boolean createFile(File parentDir, File targetFile) {
        if (parentDir == null || targetFile == null) {
            throw new NullPointerException();
        }
        if (!parentDir.isDirectory()) throw new IllegalArgumentException();

        File absoluteTargetFile = new File(parentDir, targetFile.getName());
        if (absoluteTargetFile.exists()) {
            return true;
        } else {
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            try {
                absoluteTargetFile.createNewFile();
                return true;
            } catch (IOException ex) {
                MCLoggerFactory.getLogger(FileUtils.class).warn("Can't create meta file " + absoluteTargetFile.getAbsolutePath(), ex);
            }
            return false;
        }
    }

    public static long getLastModificationTime(File target) {
        if (target == null || !target.exists()) {
            throw new IllegalArgumentException();
        }

        long time = 0;
        if (target.isFile()) {
            time = target.lastModified();
        } else if (target.isDirectory()) {
            time = target.lastModified();
            File[] files = target.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    time = Math.max(getLastModificationTime(files[i]), time);
                }
            }
        }
        return time;
    }

    public static void deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            deleteDirectory(file);
        }
    }

    public static void deleteDirectory(File directory) {
        if (!directory.exists()) {
            return;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                deleteFile(files[i]);
            }
        }

        directory.delete();
    }
}

