package com.kaixindev.android;

import java.io.File;

import android.content.Context;
import android.os.Environment;

import com.kaixindev.core.IOUtil;

/**
 * Utility functions on android file system.
 * Use this class to get file location for cache or storage purpose.
 * We have 2 places to put files. The first place is INTERNAL memory, usually means ROM.
 * The second place is external storage, such as SD card, internal flash memory, and so on.
 * Use getExternalFile or getInternalFile to get the location for data.
 * Use getExternalCacheFile or getInternalCacheFile to get the location for cache.
 * The 4 functions only return location, the file and its parent directories are not created automatically.
 * It's your responsibility to create its parent directories and the file itself.
 * @author Wilbur Luo
 */
public class FileSystem {

    /**
     * ************************************************************************
     * External Storage
     */

    /**
     * Get external directory, this directory will be deleted automatically
     * if the application is uninstalled.
     * @param context
     * @return
     */
    public static File getExternalDirectory(final Context context) {
        final File dir = Environment.getExternalStorageDirectory();
        if (dir == null) {
            return null;
        }
        final String path = dir.getAbsolutePath()
                            + File.separator + "Android"
                            + File.separator + "data"
                            + File.separator + context.getPackageName()
                            + File.separator + "files";
        return new File(path);
    }

    /**
     * Get external directory, this directory will be deleted automatically
     * if the application is uninstalled.
     * @param context
     * @param subdir
     * @return
     */
    public static File getExternalDirectory(final Context context, final String subdir) {
        final File dir = getExternalDirectory(context);
        if (dir == null) {
            return null;
        }
        final StringBuilder path = new StringBuilder();
        path.append(dir.getAbsolutePath())
                .append(File.separator)
                .append(subdir);
        return new File(path.toString());
    }

    /**
     * Delete the specified external directory.
     * @param context
     * @param subdir
     */
    public static void deleteExternalDirectory(final Context context, final String subdir)
    {
        IOUtil.recursiveDelete(getExternalDirectory(context, subdir));
    }

    /**
     * Get external file by filename, this file will be deleted automatically 
     * if the application is uninstalled.
     * @param context
     * @param subdir
     * @param filename
     * @return The file object that you request for.
     */
    public static File getExternalFile(final Context context, final String subdir, final String filename)
    {
        final File dir = getExternalDirectory(context, subdir);
        if (dir == null) {
            return null;
        }
        return new File(dir, filename);
    }

    /**
     * Get external cache directory. The contents in this directory will be deleted when 
     * {@link FileSystem#clearExternalCache clearExternalCache} is called.
     * @param context
     * @return
     */
    public static File getExternalCacheDirectory(final Context context) {
    	/*
    	final File dir = Environment.getExternalStorageDirectory();
        if (dir == null) {
            return null;
        }
        final String path = dir.getAbsolutePath()
                            + File.separator + "Android"
                            + File.separator + "data"
                            + File.separator + context.getPackageName()
                            + File.separator + "cache";
        return new File(path);
        */
        return context.getExternalCacheDir();
    }

    /**
     * Get external cache directory. The contents in this directory will be deleted when 
     * {@link FileSystem#clearExternalCache clearExternalCache} is called.
     * @param context
     * @param subdir
     * @return
     */
    public static File getExternalCacheDirectory(final Context context, final String subdir)
    {
        final File dir = getExternalCacheDirectory(context);
        if (dir == null) {
            return null;
        }
        final String path = dir.getAbsolutePath()
                            + File.separator
                            + subdir;
        return new File(path);
    }

    /**
     * Get external cache file by filename and sub-directory, this file will be deleted
     * when space is low, the application is uninstalled or {@link FileSystem#clearExternalCache clearExternalCache} is called.
     * @param context
     * @param subdir
     * @param filename
     * @return The file object that you request for.
     */
    public static File getExternalCacheFile(final Context context, final String subdir, final String filename)
    {
        final File dir = getExternalCacheDirectory(context, subdir);
        if (dir == null) {
            return null;
        }
        return new File(dir, filename);
    }

    /**
     * Clear the contents in the external cache directory.
     * @param context
     */
    public static void clearExternalCache(final Context context) {
        IOUtil.recursiveDelete(getExternalCacheDirectory(context));
    }

    /**
     * Clear the contents in the specified cache sub-directory.
     * @param context
     * @param subdir
     */
    public static void clearExternalCache(final Context context, final String subdir) {
        IOUtil.recursiveDelete(getExternalCacheDirectory(context, subdir));
    }

    /**
     * ************************************************************************
     * Internal Storage
     */

    /**
     * Get internal directory. The file will be deleted if user clear application data.
     * @param context
     * @return
     */
    public static File getInternalDirectory(final Context context) {
        return context.getFilesDir();
    }

    public static File getInternalDirectory(final Context context, final String subdir) {
        final File dir = getInternalDirectory(context);
        if (dir == null) {
            return null;
        }
        final StringBuilder path = new StringBuilder();
        path.append(dir.getAbsolutePath())
                .append(File.separator)
                .append(subdir);
        return new File(path.toString());
    }

    /**
     * Delete the specified internal directory.
     * @param context
     * @param subdir
     */
    public static void deleteInternalDirectory(final Context context, final String subdir)
    {
        IOUtil.recursiveDelete(getInternalDirectory(context, subdir));
    }

    /**
     * Get internal file by filename. 
     * This file will be deleted when user clear application data or call 
     * {@link FileSystem#clearInternalCache clearInternalCache}.
     * @param context
     * @param subdir
     * @param filename
     * @return The file object that you request for.
     */
    public static File getInternalFile(final Context context, final String subdir, final String filename)
    {
        final File dir = getInternalDirectory(context, subdir);
        if (dir == null) {
            return null;
        }
        return new File(dir, filename);
    }

    /**
     * Get internal cache directory. This directory, its sub-directories and files 
     * will be deleted when user clear application data or call 
     * {@link FileSystem#clearInternalCache clearInternalCache}.
     * @param context
     * @return
     */
    public static File getInternalCacheDirectory(final Context context) {
        return context.getCacheDir();
    }

    /**
     * Get internal cache directory. This directory, its sub-directories and files 
     * will be deleted when user clear application data or call 
     * {@link FileSystem#clearInternalCache clearInternalCache}. 
     * @param context
     * @param subdir
     * @return
     */
    public static File getInternalCacheDirectory(final Context context, final String subdir)
    {
        final File dir = getInternalCacheDirectory(context);
        if (dir == null) {
            return null;
        }
        final String path = dir.getAbsolutePath()
                            + File.separator
                            + subdir;
        return new File(path);
    }

    /**
     * Get internal cache file by filename and sub-directory.
     * This file will be deleted when user clear application data or call 
     * {@link FileSystem#clearInternalCache clearInternalCache}.
     * @param context
     * @param subdir
     * @param filename
     * @return The file object that you request for.
     */
    public static File getInternalCacheFile(final Context context, final String subdir, final String filename)
    {
        final File dir = getInternalCacheDirectory(context, subdir);
        if (dir == null) {
            return null;
        }
        return new File(dir, filename);
    }

    /**
     * Clear internal cache.
     * @param context
     */
    public static void clearInternalCache(final Context context) {
        IOUtil.recursiveDelete(getInternalCacheDirectory(context));
    }

    /**
     * Clear the specified cache sub-directory.
     * @param context
     * @param subdir
     */
    public static void clearInternalCache(final Context context, final String subdir) {
        IOUtil.recursiveDelete(getInternalCacheDirectory(context, subdir));
    }

    /**
     * If external storage state is {@link android.os.Environment#MEDIA_MOUNTED Environment.MEDIA_MOUNTED},
     * return a file in the external storage, otherwise return a file in the internal storage.
     * See {@link #getInternalFile} and {@link #getExternalFile}.
     * @param context
     * @param subdir
     * @param filename
     * @return Return null for failure.
     */
    public static File getProperFile(final Context context, final String subdir, final String filename)
    {
        if (OSUtil.isExternalStorageMounted()) {
            return getExternalFile(context, subdir, filename);
        }
        else {
            return getInternalFile(context, subdir, filename);
        }
    }

    /**
     * If external storage state is {@link android.os.Environment#MEDIA_MOUNTED Environment.MEDIA_MOUNTED},
     * return a file in the external storage cache, otherwise return a file in the internal storage cache.
     * See {@link #getInternalCacheFile} and {@link #getExternalCacheFile}.
     * @param context
     * @param subdir
     * @param filename
     * @return Return null for failure.
     */
    public static File getProperCacheFile(final Context context, final String subdir, final String filename)
    {
        if (OSUtil.isExternalStorageMounted()) {
            return getExternalCacheFile(context, subdir, filename);
        }
        else {
            return getInternalCacheFile(context, subdir, filename);
        }
    }

    /**
     * Clear both internal and external cache.
     * @param context
     */
    public static void clearCache(final Context context) {
        clearInternalCache(context);
        clearExternalCache(context);
    }

    /**
     * Clear both internal and external cache.
     * @param context
     * @param subdir
     */
    public static void clearCache(final Context context, final String subdir) {
        clearInternalCache(context, subdir);
        clearExternalCache(context, subdir);
    }

    /**
     * Delete both internal and external directories.
     * @param context
     * @param subdir
     */
    public static void deleteDirectory(final Context context, final String subdir) {
        deleteInternalDirectory(context, subdir);
        deleteExternalDirectory(context, subdir);
    }

    /**
     * Get a newer file in the specified internal or external directory.
     * @param context
     * @param subdir
     * @param filename
     * @return
     */
    public static File getNewerFile(final Context context, final String subdir, final String filename)
    {
        final File internal = getInternalFile(context, subdir, filename);
        final File external = getExternalFile(context, subdir, filename);
        return IOUtil.compare(internal, external);
    }

    /**
     * Get a newer cache file in the specified internal or external directory.
     * @param context
     * @param subdir
     * @param filename
     * @return
     */
    public static File getNewerCacheFile(final Context context, final String subdir, final String filename)
    {
        final File internal = getInternalCacheFile(context, subdir, filename);
        final File external = getExternalCacheFile(context, subdir, filename);
        return IOUtil.compare(internal, external);
    }

}
