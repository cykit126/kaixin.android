package com.kaixindev.io;

public interface OutputBuffer {
    public int write(byte[] data, int offset, int length);

    public boolean seek(long offset);

    public long getSize();

    public void close();

    public String getPath();
}
