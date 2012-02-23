package com.kaixindev.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;

import com.kaixindev.android.Log;

public class IOUtil {
    public static final String PROTOCOL_FILE = "file";
    public static final String PROTOCOL_ASSET = "asset";
    public static final String PROTOCOL_HTTP = "http";
    public static final String PROTOCOL_HTTPS = "https";

    /**
     * @param uri
     * @return
     */
    public static final boolean isRemoteFile(final String uri) {
        final String proto = getProtocol(uri);
        return proto.equals(PROTOCOL_HTTPS)
               || proto.equals(PROTOCOL_HTTP);
    }

    /**
     * Get the protocol(scheme) of the specified URI.
     * @param uri
     * @return 
     */
    public static final String getProtocol(final String uri) {
        if (uri == null || uri.length() <= 0) {
            return PROTOCOL_FILE;
        }
        final Pattern pattern = Pattern.compile("^(\\w+):/{2,3}");
        final Matcher matcher = pattern.matcher(uri);
        if (matcher.find()) {
            return matcher.group(1);
        }
        else {
            return PROTOCOL_FILE;
        }
    }

    /**
     * Open a {@link InputStream} by a URI.
     * Currently support {@link PROTOCOL_ASSET}|{@link PROTOCOL_FILE}|{@link PROTOCOL_HTTP}|{@link PROTOCOL_HTTPS} 
     * @param uri
     * @return
     */
    public static InputStream open(final Context context, final String uri) {
        final String protocol = getProtocol(uri);
        if (protocol.equals(PROTOCOL_ASSET)) {
            final String path = getPath(uri);
            try {
                return context.getAssets().open(path);
            }
            catch (final IOException e) {
                //e.printStackTrace();
                return null;
            }
        }
        else if (protocol.equals(PROTOCOL_FILE)) {
            final String path = getPath(uri);
            try {
                return new FileInputStream(path);
            }
            catch (final Exception e) {
                return null;
            }
        }
        else if (protocol.equals(PROTOCOL_HTTP) || protocol.equals(PROTOCOL_HTTPS)) {
            try {
                final URL oUrl = new URL(uri);
                return oUrl.openStream();
            }
            catch (final Exception e) {
                //e.printStackTrace();
                return null;
            }
        }
        else {
            return null;
        }
    }

    /**
     * Get the path of the give URI.
     * e.g.
     * asset://backgrounds/earth.png return s backgrounds/earth.png.
     * backgrounds/earth.png returns backgrounds/earth.png.
     * https://gaya3d.com/get/you/path returns /get/your/path.
     * @param uri
     * @return
     */
    public static String getPath(final String uri) {
        if (uri == null) {
            return null;
        }
        final Pattern pattern = Pattern.compile("^(\\w+):/{2,3}(.*)");
        final Matcher matcher = pattern.matcher(uri);
        if (matcher.find()) {
            final String proto = matcher.group(1);
            if (proto.equals(PROTOCOL_ASSET)) {
                return matcher.group(2);
            }
        }
        return uri;
    }

    /**
     * Read InputStream to buffer.
     * This should be used for small contents.
     * @param is
     * @return
     */
    public static byte[] read(final InputStream is) {
        if (is == null) {
            return null;
        }
        final ByteArrayBuffer content = new ByteArrayBuffer(0);
        final byte[] buffer = new byte[1024];
        int len = 0;
        try {
            while ((len = is.read(buffer)) > 0) {
                content.append(buffer, 0, len);
            }
        }
        catch (final IOException e) {
        }
        return content.buffer();
    }

    /**
     * Read file to buffer.
     * This should be used for small contents.
     * @param file
     * @return
     */
    public static byte[] read(final File file) {
        if (file != null && file.exists()) {
            try {
                final InputStream in = new FileInputStream(file);
                return read(in);
            }
            catch (final FileNotFoundException e) {
            }
        }
        return null;
    }

    /**
     * Write data to file.
     * @param file
     * @param data
     * @return true for success, false for failure.
     */
    public static boolean write(final File file, final byte[] data, final int offset, final int length)
    {
        if (file == null || data == null || data.length <= 0) {
            return false;
        }

        if (!file.exists()) {
            final File parent = file.getParentFile();
            if (parent != null && !parent.mkdirs()) {
                return false;
            }
            try {
                if (!file.createNewFile()) {
                    return false;
                }
            }
            catch (final IOException e) {
                Log.w(e.getMessage());
                //e.printStackTrace();
                return false;
            }
        }

        try {
            final OutputStream out = new FileOutputStream(file);
            out.write(data, offset, length);
            out.close();
            return true;
        }
        catch (final Exception e) {
            Log.e(e.getMessage());
            //e.printStackTrace();
            return false;
        }
    }

    /**
     * Copy content.
     * @param from
     * @param to
     * @return The amount of bytes have been copies.
     */
    public static int copy(final InputStream from, final OutputStream to) {
        if (from == null || to == null) {
            return 0;
        }

        final byte[] buffer = new byte[1024];
        int total = 0;
        int len = 0;
        try {
            while ((len = from.read(buffer)) > 0) {
                to.write(buffer, 0, len);
                total += len;
            }
        }
        catch (final IOException e) {
        }

        return total;
    }

    /**
     * Copy content.
     * @param from
     * @param to If not exist, will be created.
     * @return The amount of bytes have been copies.
     */
    public static int copy(final File from, final File to) {
        if (from == null || to == null) {
            return 0;
        }

        if (!from.exists()) {
            return 0;
        }

        if (!to.exists()) {
            final File parent = to.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                return 0;
            }
        }
        try {
            return copy(new FileInputStream(from), new FileOutputStream(to));
        }
        catch (final FileNotFoundException e) {
            return 0;
        }
    }

    /**
     * Open output stream, file is created if it doesn't exist.
     * @param path
     * @return Returns null for failure.
     */
    public static OutputStream openOutputStream(final String path) {
        if (path == null || path.length() <= 0) {
            return null;
        }
        return openOutputStream(new File(path));
    }

    /**
     * Open output stream, file is created if it doesn't exist.
     * @param file
     * @return Returns null for failure.
     */
    public static OutputStream openOutputStream(final File file) {
        if (file == null || !createFile(file)) {
            return null;
        }
        try {
            return new FileOutputStream(file);
        }
        catch (final FileNotFoundException e) {
            return null;
        }
    }

    /**
     * Open file as InputStream.
     * @param file
     * @return Return null for failure.
     */
    public static InputStream openInputStream(final String path) {
        if (path == null || path.length() <= 0) {
            return null;
        }
        return openInputStream(new File(path));
    }

    /**
     * Open file as InputStream.
     * @param file
     * @return Return null for failure.
     */
    public static InputStream openInputStream(final File file) {
        if (file == null) {
            return null;
        }
        try {
            return new FileInputStream(file);
        }
        catch (final FileNotFoundException e) {
            return null;
        }
    }

    /**
     * Create parent directories if necessary.
     * @param file
     * @return true for success, false for failure.
     */
    public static boolean createParentDirectories(final File file) {
        if (file == null) {
            return false;
        }
        final File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            return parent.mkdirs();
        }
        else {
            return true;
        }
    }

    /**
     * Create parent directories if necessary.
     * @param file
     * @return true for success, false for failure.
     */
    public static boolean createParentDirectories(final String file) {
        return createParentDirectories(new File(file));
    }

    /**
     * Create a file if it doesn't exist.
     * @param file
     * @return true for success, false for failure.
     */
    public static boolean createFile(final File file) {
        if (file == null) {
            return false;
        }
        if (!file.exists()) {
            if (!createParentDirectories(file)) {
                return false;
            }
            try {
                return file.createNewFile();
            }
            catch (final IOException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Create a file if it doesn't exist.
     * @param file
     * @return true for success, false for failure.
     */
    public static boolean createFile(final String file) {
        if (file == null || file.length() <= 0) {
            return false;
        }
        return createFile(new File(file));
    }

    /**
     * Compares files.
     * @param left
     * @param right
     * @return Returns the newer file or null if both of the files are null or not existent.
     */
    public static File compare(final File left, final File right) {
        if (left == null) {
            return right;
        }
        if (right == null || !right.exists()) {
            return left;
        }
        if (!left.exists()) {
            return right;
        }
        return left.lastModified() > right.lastModified() ? left : right;
    }

    /**
     * Delete file recursively.
     * If it's a file, delete file;
     * If it's a directory, delete the directory and its sub-directories and files.
     * @param file
     */
    public static void recursiveDelete(final File file) {
        try {
            if (file != null && file.exists()) {
                if (file.isDirectory()) {
                    for (final File tmp : file.listFiles()) {
                        recursiveDelete(tmp);
                    }
                }
                file.delete();
            }
        }
        catch (final Exception e) {

        }
    }
}
