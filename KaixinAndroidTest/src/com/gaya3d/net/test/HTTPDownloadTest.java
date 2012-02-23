package com.gaya3d.net.test;

import java.io.File;

import com.gaya3d.android.FileSystem;
import com.gaya3d.net.HTTPDownload;

import android.test.AndroidTestCase;

public class HTTPDownloadTest extends AndroidTestCase {
    
    public void testRequest() {
        File logFile = FileSystem.getInternalCacheFile(getContext(), "HTTPDownloadTest", "testRequest.log");
        if (logFile.exists()) {
            logFile.delete();
        }
        logFile.deleteOnExit();
        File dataFile = FileSystem.getInternalCacheFile(getContext(), "HTTPDownloadTest", "testRequest.data");
        if (logFile.exists()) {
            logFile.delete();
        }
        logFile.deleteOnExit();
        
        HTTPDownload download = new HTTPDownload();
        int ret = download.perform("http://61.180.49.137/new_bg/Asteroid.gpk", dataFile.getAbsolutePath(), logFile.getAbsolutePath(), null);
        assertEquals(HTTPDownload.RESULT_NEW,ret);
        
        ret = download.perform("http://61.180.49.137/new_bg/Asteroid.gpk", dataFile.getAbsolutePath(), logFile.getAbsolutePath(), null);
        assertEquals(HTTPDownload.RESULT_NOT_MODIFIED,ret);
    }
    
}
