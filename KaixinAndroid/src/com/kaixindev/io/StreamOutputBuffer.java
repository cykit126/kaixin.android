package com.kaixindev.io;

import java.io.IOException;
import java.io.OutputStream;

public class StreamOutputBuffer implements OutputBuffer {

    public final OutputStream mOutputStream;

    public StreamOutputBuffer(final OutputStream s) {
        mOutputStream = s;
    }

    @Override
    public int write(final byte[] data, final int offset, final int length) {
        try {
            mOutputStream.write(data, offset, length);
            return length;
        }
        catch (final IOException e) {
            return -1;
        }
    }

    @Override
    public boolean seek(final long offset) {
        return false;
    }

    @Override
    public void close() {
        try {
            mOutputStream.close();
        }
        catch (final IOException e) {
        }
    }

    @Override
    public long getSize() {
        return 0;
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
