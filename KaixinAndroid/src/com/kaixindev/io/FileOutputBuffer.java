package com.kaixindev.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.kaixindev.core.IOUtil;

public class FileOutputBuffer implements OutputBuffer {

    private final RandomAccessFile mRandomFile;
    private final File mFile;

    private FileOutputBuffer(final RandomAccessFile randomFile, final File file) {
        mRandomFile = randomFile;
        mFile = file;
    }

    @Override
    public void finalize() {
        try {
            mRandomFile.close();
        }
        catch (final IOException e) {

        }
    }

    public static FileOutputBuffer newInstance(final File file, final String mode) {
        if (file == null) {
            return null;
        }
        if (!IOUtil.createFile(file)) {
            return null;
        }
        try {
            final RandomAccessFile randomFile = new RandomAccessFile(file, mode);
            return new FileOutputBuffer(randomFile, file);
        }
        catch (final FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public int write(final byte[] data, final int offset, final int length) {
        try {
            mRandomFile.write(data, offset, length);
            return length;
        }
        catch (final IOException e) {
            return -1;
        }
    }

    @Override
    public boolean seek(final long offset) {
        try {
            mRandomFile.seek(offset);
            return true;
        }
        catch (final IOException e) {
            return false;
        }
    }

    @Override
    public long getSize() {
        return mFile.length();
    }

    @Override
    public void close() {
        try {
            mRandomFile.close();
        }
        catch (final IOException e) {
        }
    }

    @Override
    public String getPath() {
        return mFile.getAbsolutePath();
    }
}
