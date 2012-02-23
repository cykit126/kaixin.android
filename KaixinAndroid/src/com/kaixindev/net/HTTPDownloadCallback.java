package com.kaixindev.net;

import java.io.RandomAccessFile;

public interface HTTPDownloadCallback {
    /**
     * 
     * @param url
     * @param current
     * @param total
     * @return Returning false means to abort download. 
     */
    public boolean onProgress(String url, long current, long total);

    public void onError(String url, int statusCode);

    public void onCompleted(String url, RandomAccessFile output, StreamInfoFile log, boolean isNotModified);

    public void onTimeout(String url);

    /**
     * 
     * @param url
     * @return Returning false means to abort download. 
     */
    public boolean onBeginReceivingBody(String url, long total);

    /**
     * 
     * @param url
     * @param data
     * @param length
     * @return Returning false means to abort download. 
     */
    public boolean onReceiveBody(String url, byte[] data, int length);
}
