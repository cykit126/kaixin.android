package com.kaixindev.android.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.kaixindev.android.Log;
import com.kaixindev.core.IOUtil;

import android.content.Context;
import android.content.Intent;

/**
 * FileWriterHandler provides file copying and writing service.
 * @author Wilbur Luo
 */
public class FileWriterHandler {
    
    public static final String PROPERTY_URI = "uri";
    public static final String PROPERTY_DEST = "dest";
    public static final String PROPERTY_NOTIFY_PROGRESS = "notify_progress";
    public static final String PROPERTY_CURRENT = "current";
    public static final String PROPERTY_TOTAL = "total";
    public static final String PROPERTY_CONTENT = "content";
    public static final int RESULT_OK = 0;
    public static final int RESULT_PROGRESS = 1;
    public static final int RESULT_ERROR = 2;

    /**
     * Copy file.
     * Set PROPERTY_NOTIFY_PROGRESS to true to receive progress notification.
     * Notification intent:
     * PROPERTY_RESULT: RESULT_PROGRESS.
     * PROPERTY_CURRENT: size copied, intent long extra.
     * PROPERTY_TOTAL: total size to copy, intent long extra.
     * @param PROPERTY_SRC required, intent String extra.
     * @param PROPERTY_DEST required, intent String extra.
     * @param PROPERTY_NOTIFY_PROGRESS optional, default to true, intent boolean extra.
     * @return Intent with JOB_RESULT set to RESULT_OK|RESULT_ERROR|RESULT_PROGRESS.
     */
    public static final String ACTION_WRITE_CONTENT = "com.gaya3d.android.service.FileWriterHandler.WRITE_CONTENT";
    
    /**
     * Write content to file.
     * NOTICE: Should small content be written by this service.
     * Large content takes too much memory, use {@link copy} service.
     * @param PROPERTY_DEST required, intent String extra.
     * @param PROPERTY_CONTENT required, intent byte[] extra.
     * @param PROPERTY_NOTIFY_PROGRESS optional, default to true, intent boolean extra.
     * @return Intent with JOB_RESULT set to RESULT_OK|RESULT_ERROR|RESULT_PROGRESS.
     */
    public static final String ACTION_WRITE_URI = "com.gaya3d.android.service.FileWriterHandler.WRITE_URI";
    
    @IntentHandler(action=ACTION_WRITE_CONTENT)
    public void writeContent(final Intent intent, final Object context, final Object unused) {
        AsyncService service = (AsyncService)context;
        
        // Check input.
        String dest = intent.getStringExtra(PROPERTY_DEST);
        if (dest == null || dest.length() <= 0) {
            sendResult(service,RESULT_ERROR,intent);
            return;
        }
        byte[] content = intent.getByteArrayExtra(PROPERTY_CONTENT);
        if (content == null || content.length <= 0) {
            sendResult(service,RESULT_ERROR,intent);
            return; 
        }
        
        ///////////////////////////////////////////////////////////////////////
        OutputStream out = IOUtil.openOutputStream(dest);
        if (out == null) {
            sendResult(service,RESULT_ERROR,intent);
            return;           
        }
        try {
            out.write(content);
            sendResult(service,RESULT_OK,intent);
            return;  
        }
        catch (IOException e) {
            Log.w(e.getMessage());
            e.printStackTrace();
            sendResult(service,RESULT_ERROR,intent);
            return;  
        }
    }
    
    @IntentHandler(action=ACTION_WRITE_URI)
    public void writeURI(final Intent intent, final Object context, final Object unused) {
        AsyncService service = (AsyncService)context;
        
        // Check input.
        String src = intent.getStringExtra(PROPERTY_URI);
        if (src == null || src.length() <= 0) {
            sendResult(service,RESULT_ERROR,intent);
            return;
        }
        String dest = intent.getStringExtra(PROPERTY_DEST);
        if (dest == null || dest.length() <= 0) {
            sendResult(service,RESULT_ERROR,intent);
            return;
        }
        boolean notifyProgress = intent.getBooleanExtra(PROPERTY_NOTIFY_PROGRESS, false);
        
        ///////////////////////////////////////////////////////////////////////
        InputStream in = IOUtil.open(service,src);
        if (in == null) {
            sendResult(service,RESULT_ERROR,intent);
            return;
        }
        OutputStream out = IOUtil.openOutputStream(dest);
        if (out == null) {
            sendResult(service,RESULT_ERROR,intent);
            return;           
        }
        
        File srcFile = new File(src);
        byte[] buffer = new byte[4096];
        int len = 0;
        long current = 0;
        long total = srcFile.length();
        try {
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer,0,len);
                current += (long)len;
                if (notifyProgress) {
                    sendProgressResult(service,current,total,intent);
                }
            }
            sendResult(service,RESULT_OK,intent);
        }
        catch (IOException e) {
            Log.w(e.getMessage());
            e.printStackTrace();
            sendResult(service,RESULT_ERROR,intent);
            return;
        }
    }
    
    /**
     * ************************************************************************
     * Private methods.
     */
    
    private void sendResult(Context context, int result, Intent origin) {
        Intent resp = AsyncService.createResponseIntent(origin);
        if (resp != null) {
            resp.putExtra(AsyncService.JOB_RESULT,result);
            context.sendBroadcast(resp);
        }
    }
    
    private void sendProgressResult(Context context, long current, long total, Intent origin) {
        Intent resp = AsyncService.createResponseIntent(origin);
        if (resp != null) {
            resp.putExtra(AsyncService.JOB_RESULT,RESULT_PROGRESS);
            resp.putExtra(PROPERTY_CURRENT,current);
            resp.putExtra(PROPERTY_TOTAL,total);
            context.sendBroadcast(resp);
        }
    }
}










