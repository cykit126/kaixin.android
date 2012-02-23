package com.kaixindev.net;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;

import com.kaixindev.android.Log;
import com.kaixindev.core.IOUtil;

public class HTTPDownload {
    
    public static final int RESULT_NEW = 0;
    public static final int RESULT_NOT_MODIFIED = 1;
    public static final int RESULT_FAILED = 2;
    public static final int RESULT_ABORTED = 3;

    public static final int FLAG_PREALLOC = 1;

    /////////////////////////////////////////////////////////
    private static final int BUFFER_SIZE = 4096;
    
    private int mFlags = 0;
    private int mTimeoutMS = 0;
    private long mNotifyingInterval = 1000; //1second

    public HTTPDownload() {
        mFlags = 0;
    }
    
    //////////////////////////////////////////////////////////
    public HTTPDownload(final int flags) {
        mFlags = flags;
    }

    /**
     * Set flags.
     * @param flags
     */
    public void setFlags(final int flags) {
        mFlags = flags;
    }

    /**
     * Set flags.
     * @return
     */
    public int getFlags() {
        return mFlags;
    }
    
    /**
     * Get notification interval, now is only for progress notification.
     * @return Value in milliseconds.
     */
    public long getNotifyingInterval() {
        return mNotifyingInterval;
    }

    /**
     * Set notification interval, now is only for progress notification.
     * @param value In milliseconds.
     */
    public void setNotifyingInterval(long value) {
        if (value < 0) {
            value = 0;
        }
        mNotifyingInterval = value;
    }

    /**
     * Perform download.
     * @param url URL to download.
     * @param filePath File path to store data, can't be null.
     * @param logFilePath File path to store information, can't be null.
     * @param callback Set to null if you don't need to receive callback event.
     * @return Returns result code. 
     *      {@link HTTPDownload.NEW}|{@link HTTPDownload.NOT_MODIFIED}|{@link HTTPDownload.FAILED}|{@link HTTPDownload.ABORTED}
     */
    public int perform(String url, final String filePath, final String logFilePath, final HTTPDownloadCallback callback)
    {
        HttpURLConnection conn = createConnection(url);
        if (conn == null) {
            return RESULT_FAILED;
        } 
        if (filePath == null || filePath.length() <= 0
                || logFilePath == null || logFilePath.length() <= 0) 
        {
            return RESULT_FAILED;
        }
        
        if (!IOUtil.createParentDirectories(filePath)) {
            Log.e("Couldn't create directories for " + filePath);
            return RESULT_FAILED;
        }
        if (!IOUtil.createParentDirectories(logFilePath)) {
            Log.e("Couldn't create directories for " + logFilePath);
            return RESULT_FAILED;
        }
        
        // Calculate sizes and get last-modified time.
        String lastModified = StreamInfoFile.formateTime(0);
        RandomAccessFile dataFile = null;
        try {
            dataFile = new RandomAccessFile(filePath, "rw");
        }
        catch (FileNotFoundException e) {
            Log.w(e.getMessage());
            e.printStackTrace();
        }
        
        StreamInfoFile logFile = StreamInfoFile.newInstance(logFilePath);
        if (logFile == null) {
            Log.e("Failed to create log file " + logFilePath);
            return RESULT_FAILED;
        }
        
        long currentSize = 0;
        long totalSize = 0;
        if (logFile.isOK()) {
            //Log.d(logFilePath + "is a valid log file.");
            long[] sizes = calculateSizes(dataFile,logFile);
            currentSize = sizes[0];
            totalSize = sizes[1];
            lastModified = logFile.getLastModifiedString();
        } else {
            //Log.d(logFilePath + "is not a valid log file.");
            if (!logFile.init()) {
                Log.w("Can't initialize log file " + logFilePath);
                return RESULT_FAILED;
            }
        }
        //Log.d("last modified:" + lastModified);
        //Log.d("current size: " + currentSize + ", total size:" + totalSize);
        
        // Set HTTP request headers.
        if (currentSize > 0) {
            if (currentSize == totalSize) {
                conn.addRequestProperty("If-Modified-Since", lastModified);
            }
            else {
                final StringBuilder range = new StringBuilder();
                range.append("bytes=")
                        .append(currentSize)
                        .append("-")
                        .append(totalSize);
                conn.addRequestProperty("Range", range.toString());
                conn.addRequestProperty("If-Range", lastModified);
            }
        }
        
        // Perform connecting.
        int statusCode = -1;
        try {
            conn.connect();
            statusCode = conn.getResponseCode();
        }
        catch (IOException e) {
            Log.w(e.getMessage());
            e.printStackTrace();
            conn.disconnect();
            closeFile(dataFile);
            return RESULT_FAILED;
        }
        
        int ret = 0;
        switch (statusCode) {
        // Not modified
        case 304: 
            ret = RESULT_NOT_MODIFIED;
            break;
            
        // Receive content from remote server.
        case 200:
        case 206:
            ret = receiveData(url, conn, dataFile, logFile, callback);
            break;
            
        // Errors occured.
        default:
            Log.i("Status code:" + statusCode + ", can't fetch " + url);
            ret = RESULT_FAILED;
            break;
        }
       
        switch (ret) {
        case RESULT_NOT_MODIFIED:
            Log.i(url + " is not modified.");
            if (callback != null) {
                callback.onCompleted(url, dataFile, logFile, true);
            } 
            break;
        case RESULT_NEW:
            if (callback != null) {
                callback.onCompleted(url, dataFile, logFile, true);
            } 
            break;
        case RESULT_ABORTED:
            break;
        case RESULT_FAILED:
            if (callback != null) {
                callback.onError(url, statusCode);
            }
            break;
        }
        
        // Release resources.
        if (conn != null) {
            conn.disconnect();
        }
        closeFile(dataFile);
        if (logFile != null) {
            logFile.close();
        }
        
        return ret;
    }

    /**
     * Set timeout.
     * @param timeoutMS
     */
    public void setTimeout(final int timeoutMS) {
        mTimeoutMS = timeoutMS;
    }

    /**
     * Get timeout.
     * @return
     */
    public int getTimeout() {
        return mTimeoutMS;
    }
    
    /**
     * ************************************************************************
     * Private methods.
     */
    
    private HttpURLConnection createConnection(String URL) {
        URL oUrl = null;
        try {
            oUrl = new URL(URL);
        }
        catch (MalformedURLException e) {
            Log.w(e.getMessage());
            e.printStackTrace();
            return null;
        }
        
        // Open connection.
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection)oUrl.openConnection();
        }
        catch (IOException e) {
            Log.w(e.getMessage());
            e.printStackTrace();
            return null;
        }
        conn.setInstanceFollowRedirects(true);
        conn.setUseCaches(false);
        
        return conn;
    }
    
    /**
     * Calculate size from log file and data file.
     * @param dataFile
     * @param logFile
     * @return long[] {currentSize,totalSize}
     */
    private long[] calculateSizes(RandomAccessFile dataFile, StreamInfoFile logFile) {
        long currentSize = 0;
        long totalSize = 0;
        //lastModified = StreamInfoFile.formateTime(logFile.getLastModified());
        //Log.d("last modified:" + lastModified);
        
        currentSize = logFile.getCurrentSize();
        //Log.d("current size in log file: " + currentSize + ", output file length:" + dataFile.length());
        try {
            if (currentSize > dataFile.length()) {
                currentSize = 0;
            }
        }
        catch (IOException e) {
            Log.w(e.getMessage());
            e.printStackTrace();
        }

        totalSize = logFile.getTotalSize();
        try {
            if (totalSize == 0
                || totalSize > dataFile.length()
                || currentSize > totalSize) {
                totalSize = -1;
                currentSize = 0;
            }
        }
        catch (IOException e) {
            Log.w(e.getMessage());
            e.printStackTrace();
        }
        
        //Log.d("total size in log file:" + totalSize);
        return new long[] {currentSize,totalSize};      
    }
    
    /**
     * 
     * @param file
     */
    private void closeFile(RandomAccessFile file) {
        try {
            file.close();
        }
        catch (IOException e) {
            Log.w(e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * @param url
     * @param conn
     * @param dataFile
     * @param logFile
     * @param callback
     * @return
     */
    private int receiveData(
            String url,
            HttpURLConnection conn, 
            RandomAccessFile dataFile, 
            StreamInfoFile logFile,
            HTTPDownloadCallback callback) {
        // Read HTTP response headers and write log file
        final long newLastModified = conn.getLastModified();
        if (newLastModified > 0) {
            if (logFile != null && !logFile.setLastModified(newLastModified)) {
                Log.w("Can't write last modified time to log file.");
            }
        }

        // Read Content-Range from response headers
        long totalSize = conn.getContentLength();
        long currentSize = 0;
        final String contentRange = conn.getHeaderField("Content-Range");
        if (contentRange != null && contentRange.length() > 0) {
            final int[] range = StreamInfoFile.parseContentRange(contentRange);
            currentSize = range[0];
            if (range[1] > 0) {
                totalSize = range[1];
            }
        }

        // Write current and total size into log file.
        int which = 0;
        if (currentSize < 0) {
            currentSize = 0;
        }
        if (!logFile.setCurrentSize(which, currentSize)) {
            Log.w("Can't write current size into log file.");
        }
        
        try {
            dataFile.seek(currentSize);
            
            if (totalSize < 0) {
                totalSize = 0;
            }
            if (!logFile.setTotalSize(totalSize)) {
                Log.w("Can't write total size into log file.");
            }
            // Resize the file.
            if (((mFlags & FLAG_PREALLOC) == FLAG_PREALLOC || dataFile.length() > totalSize)) {
                dataFile.setLength(totalSize);
            }
            
            if (callback != null && !callback.onBeginReceivingBody(url,totalSize))
                return RESULT_ABORTED;

            long lastNotify = 0;
            final InputStream input = conn.getInputStream();
            final byte[] buffer = new byte[BUFFER_SIZE];
            int bufSize = 0;
            while ((bufSize = input.read(buffer)) > 0) {
                dataFile.write(buffer, 0, bufSize);
                currentSize += bufSize;
                if (!logFile.setCurrentSize(which, currentSize)) {
                    Log.w("Can't write current size to log file.");
                }
                
                long now = System.currentTimeMillis();
                if (callback != null && now - lastNotify >= mNotifyingInterval) {
                    lastNotify = now;
                    // Synchronize the data to the sdcard is very slow.
                    // outputFile.getFD().sync();
                    if (!callback.onProgress(url, currentSize, totalSize)) {
                        return RESULT_ABORTED;
                    }
                    if (!callback.onReceiveBody(url, buffer, bufSize)) {
                        return RESULT_ABORTED;
                    }
                }
                ++which;
            }
            return RESULT_NEW;
        } catch (Exception e) {
            Log.w(e.getMessage());
            e.printStackTrace();
            return RESULT_FAILED;
        }        
    }
}















