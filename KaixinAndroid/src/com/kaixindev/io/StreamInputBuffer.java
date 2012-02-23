package com.kaixindev.io;

import java.io.IOException;
import java.io.InputStream;

public class StreamInputBuffer implements InputBuffer {

    private final InputStream mInputStream;

    public StreamInputBuffer(final InputStream s) {
        mInputStream = s;
    }

    @Override
    public int read(final byte[] buffer, final int offset, final int length) {
        try {
            return mInputStream.read(buffer, offset, length);
        }
        catch (final IOException e) {
        	e.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean seek(final long offset) {
        mInputStream.mark((int)offset);
        try {
            mInputStream.reset();
        }
        catch (final IOException e) {
            return false;
        }
        return true;
    }

    private long mSize = 0;

    @Override
    public long getSize() {
        return mSize;
    }

    public void setSize(final long size) {
        mSize = size;
    }

    @Override
    public void close() {
        try {
            mInputStream.close();
        }
        catch (final IOException e) {

        }
    }

    private String mPath;

    @Override
    public String getPath() {
        return mPath;
    }

    public void setPath(final String path) {
        mPath = path;
    }
}
