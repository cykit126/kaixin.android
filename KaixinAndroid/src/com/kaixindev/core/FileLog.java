package com.kaixindev.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kaixindev.android.Log;

public class FileLog {

    /////////////////////////////////////////////////////////
    private static final int[] DOGTAG_SIZE1 = { 0xDE, 0xAD, 0xBE, 0xEF };
    private static final int[] DOGTAG_SIZE2 = { 0xCC, 0xCC, 0xCC, 0xCC };
    private static final int[] DOGTAG_HEADER = {
            0xDE, 0xAD, 0xBE, 0xEF,
            0x49, 0x4C, 0x48, 0x48,
            0x49, 0x4C, 0x48, 0x48,
            0xDE, 0xAD, 0xBE, 0xEF,
    };
    private static final long OFFSET_LAST_MODIFIED = DOGTAG_HEADER.length;
    private static final long OFFSET_TOTAL_SIZE = OFFSET_LAST_MODIFIED + 16;
    private static final long OFFSET_CURRENT_SIZE1 = OFFSET_TOTAL_SIZE + 16;
    private static final long OFFSET_CURRENT_SIZE2 = OFFSET_CURRENT_SIZE1 + 16;
    private static final long HEADER_SIZE = OFFSET_CURRENT_SIZE2 + 16;

    /**
     * ************************************************************************
     * Properties
     */
    private RandomAccessFile mFile = null;
    private int mWhich = 0;

    /**
     * ************************************************************************
     * Methods
     */
    public FileLog(final RandomAccessFile file) {
        mFile = file;
    }

    public void Finalize() {
        close();
    }

    /**
     * 
     */
    public void close() {
        if (mFile != null) {
            try {
                mFile.close();
            }
            catch (final IOException e) {
                Log.w(e.getMessage());
                e.printStackTrace();
            }
            finally {
                mFile = null;
            }
        }
    }

    /**
     * Create new instance from path.
     * @param path
     * @return Returns null if failed.
     */
    public static FileLog newInstance(final String path) {
        if (path != null && path.length() > 0) {
            try {
                final RandomAccessFile file = new RandomAccessFile(path, "rw");
                return new FileLog(file);
            }
            catch (final FileNotFoundException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Create new instance from file.
     * @param file
     * @return Returns null if failed.
     */
    public static FileLog newInstance(final File file) {
        if (file != null) {
            return newInstance(file.getAbsolutePath());
        }
        else {
            return null;
        }
    }

    /**
     * Initialize file.
     * @param mFile
     * @return Returns true for success, false for failure.
     */
    public boolean init() {
        try {
            final long oldOffset = mFile.getFilePointer();
            mFile.setLength(HEADER_SIZE);
            // Header dog tag
            if (!writeBytes(mFile, DOGTAG_HEADER)) {
                mFile.seek(oldOffset);
                return false;
            }
            // Last modified time
            if (!writeSize(mFile, OFFSET_LAST_MODIFIED, 0)) {
                mFile.seek(oldOffset);
                return false;
            }
            // Total size
            if (!writeSize(mFile, OFFSET_TOTAL_SIZE, 0)) {
                mFile.seek(oldOffset);
                return false;
            }
            // Current size 1
            if (!writeSize(mFile, OFFSET_CURRENT_SIZE1, 0)) {
                mFile.seek(oldOffset);
                return false;
            }
            // Current size 2
            if (!writeSize(mFile, OFFSET_CURRENT_SIZE2, 0)) {
                mFile.seek(oldOffset);
                return false;
            }
            mFile.seek(oldOffset);
            return true;
        }
        catch (final IOException e) {
            return false;
        }
    }

    /**
     * Whether this file is OK or not.
     * @return true for yes, false for no.
     */
    public boolean isOK() {
        try {
            if (mFile.length() != HEADER_SIZE) {
                //Log.d("Invalid log file size:" + mFile.length());
                return false;
            }
            if (!compareInBytes(mFile, 0, DOGTAG_HEADER)) {
                //Log.d("Invalid header dog tag");
                return false;
            }
            return true;
        }
        catch (final IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get total size.
     * @return Returns total size.
     */
    public long getTotalSize() {
        return readSize(mFile, OFFSET_TOTAL_SIZE, 0);
    }

    /**
     * Set total size.
     * @param size
     * @return Returns true if success, otherwise returns false.
     */
    public boolean setTotalSize(final long size) {
        return writeSize(mFile, OFFSET_TOTAL_SIZE, size);
    }

    /**
     * Get current size.
     * @return Returns current size.
     */
    public long getCurrentSize() {
        final long size1 = readSize(mFile, OFFSET_CURRENT_SIZE1, 0);
        final long size2 = readSize(mFile, OFFSET_CURRENT_SIZE2, 0);
        return size1 >= size2 ? size1 : size2;
    }

    /**
     * Set current size.
     * @param which
     * @param size
     * @return Returns true if success, otherwise returns false.
     */
    public boolean setCurrentSize(final long size) {
        try {
            final int which = mWhich % 2;
            mWhich++;
            final long oldOffset = mFile.getFilePointer();
            if (which % 2 == 0) {
                mFile.seek(OFFSET_CURRENT_SIZE1);
                int[] dogTag = DOGTAG_SIZE1;
                if (mFile.readUnsignedByte() == dogTag[0]) {
                    dogTag = DOGTAG_SIZE2;
                }
                mFile.seek(oldOffset);
                return writeSizeEx(mFile, OFFSET_CURRENT_SIZE1, size, dogTag);
            }
            else {
                mFile.seek(OFFSET_CURRENT_SIZE2);
                int[] dogTag = DOGTAG_SIZE2;
                if (mFile.readUnsignedByte() == dogTag[0]) {
                    dogTag = DOGTAG_SIZE2;
                }
                mFile.seek(oldOffset);
                return writeSizeEx(mFile, OFFSET_CURRENT_SIZE2, size, dogTag);
            }
        }
        catch (final IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get last modified time.
     * @param file
     * @return Returns timestamp.
     */
    public long getLastModified() {
        return readSize(mFile, OFFSET_LAST_MODIFIED, 0);
    }

    /**
     * Get last modified time string.
     * @return
     */
    public String getLastModifiedString() {
        return formateTime(getLastModified());
    }

    /**
     * Set last modified time.
     * @param size
     * @return Returns true if success, otherwise returns false.
     */
    public boolean setLastModified(final long lastModified) {
        return writeSize(mFile, OFFSET_LAST_MODIFIED, lastModified);
    }

    /**
     * Format timestamp into standard string format.
     * @param timestamp
     * @return String represents the timestamp.
     */
    public static String formateTime(final long timestamp) {
        final SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(new Date(timestamp)) + " GMT";
    }

    /**
     * Parse content range from string.
     * @param data
     * @return A integer array. The first item is start offset, the second item is end offset.
     */
    public static int[] parseContentRange(final String data) {
        final Pattern p1 = Pattern.compile("^bytes (\\-?\\d+)\\-(\\-?\\d+)\\/(\\-?\\d+)");
        final Matcher m1 = p1.matcher(data);
        if (m1.find()) {
            return new int[] {
                    Integer.parseInt(m1.group(1)),
                    Integer.parseInt(m1.group(3)), };
        }

        final Pattern p2 = Pattern.compile("^bytes (\\-?\\d+|\\*)\\/(\\-?\\d+|\\*)");
        final Matcher m2 = p2.matcher(data);
        if (m2.find()) {
            return new int[] {
                    Integer.parseInt(m1.group(1)),
                    Integer.parseInt(m1.group(2)), };
        }

        return new int[] { 0, 0 };
    }

    /**
     * Get file.
     * @return
     */
    public RandomAccessFile getFile() {
        return mFile;
    }

    /**
     * ************************************************************************
     * Private methods.
     */

    /**
     * Compares data from the specified offset with the provided data.
     * @param file 
     * @param offset
     * @param data
     * @return Returns true for yes, false for no.
     */
    //////////////////////////////////////////////////////////
    private static boolean compareInBytes(final RandomAccessFile file, final long offset, final int[] data)
    {
        if (data == null) {
            return false;
        }
        try {
            final long oldOffset = file.getFilePointer();
            file.seek(offset);
            for (int i = 0; i < data.length; ++i) {
                final int b = file.readUnsignedByte();
                if (b != data[i]) {
                    file.seek(oldOffset);
                    return false;
                }
            }
            return true;
        }
        catch (final IOException e) {
            return false;
        }
    }

    /**
     * Writes data into bytes representing in integer.
     * @param file
     * @param data
     * @return
     */
    private static boolean writeBytes(final RandomAccessFile file, final int[] data) {
        if (file == null || data == null) {
            return false;
        }
        try {
            for (int i = 0; i < data.length; ++i) {
                file.writeByte(data[i]);
            }
            return true;
        }
        catch (final IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verify dog tag at the specified offset.
     * @param file
     * @param offset
     * @param dogTag
     * @return Returns true for yes, false for no.
     */
    private static boolean isDogTag(final RandomAccessFile file, final long offset, final int[] dogTag)
    {
        try {
            final long oldOffset = file.getFilePointer();
            file.seek(offset);
            final boolean ret = file.readUnsignedByte() == dogTag[0]
                                 && file.readUnsignedByte() == dogTag[1]
                                 && file.readUnsignedByte() == dogTag[2]
                                 && file.readUnsignedByte() == dogTag[3];
            file.seek(oldOffset);
            return ret;
        }
        catch (final IOException e) {
            return false;
        }
    }

    /**
     * Write size to the specified offset with dog tag.
     * @param file
     * @param offset
     * @param size
     * @param dogTag
     * @return Returns true for success, false for failure.
     */
    private static boolean writeSizeEx(final RandomAccessFile file, final long offset, final long size, final int[] dogTag)
    {
        if (file == null || offset < 0 || dogTag == null || dogTag.length != 4) {
            return false;
        }

        try {
            final long oldOffset = file.getFilePointer();
            file.seek(offset);

            if (!writeBytes(file, dogTag)) {
                file.seek(oldOffset);
                return false;
            }

            file.writeLong(size);

            if (!writeBytes(file, dogTag)) {
                file.seek(oldOffset);
                return false;
            }

            file.seek(oldOffset);
        }
        catch (final IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Write size to the specified offset.
     * @param file
     * @param offset
     * @param size
     * @return Returns true for success, false for failure.
     */
    private boolean writeSize(final RandomAccessFile file, final long offset, final long size)
    {
        return writeSizeEx(file, offset, size, DOGTAG_SIZE1);
    }

    /**
     * Read size at the specified offset.
     * @param file
     * @param offset
     * @param defValue If can't read from the specified offset, returns this value.
     * @return Returns size.
     */
    private static long readSize(final RandomAccessFile file, final long offset, final long defValue)
    {
        if (file == null || offset < 0) {
            return defValue;
        }
        try {
            final long oldOffset = file.getFilePointer();
            int[] dogTag = DOGTAG_SIZE1;
            if (!isDogTag(file, offset, dogTag)) {
                dogTag = DOGTAG_SIZE2;
                if (!isDogTag(file, offset, dogTag)) {
                    file.seek(oldOffset);
                    Log.w("Invalid size dog tag.");
                    return defValue;
                }
            }
            file.seek(offset + 4);
            final long size = file.readLong();
            if (!isDogTag(file, offset, dogTag)) {
                file.seek(oldOffset);
                Log.w("Invalid size dog tag.");
                return defValue;
            }
            return size;
        }
        catch (final IOException e) {
            e.printStackTrace();
            return defValue;
        }
    }
}
