package com.kaixindev.io;

public interface InputBuffer {
    public int read(byte[] buffer, int offset, int length);

    public boolean seek(long offset);

    public long getSize();

    public void close();

    public String getPath();
}
