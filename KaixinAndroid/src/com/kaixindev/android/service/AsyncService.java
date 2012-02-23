package com.kaixindev.android.service;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.kaixindev.android.Log;
import com.kaixindev.core.ThreadWorker;

/**
 * We trace all the jobs with job id, which can't be null or empty.
 * Jobs can be canceled if the job implementation follows the rule.
 * Rule:
 * Job should call {@link popInterrupt} to check if possible. If {@link popInterrupt}
 * returns non-null object, which is a {@link android.content.Intent}, that means it's canceled.
 * Extra information can be set in that {@link android.content.Intent}.
 * 
 * NOTICE:
 * All intents sent to this service must have extra {@link JOB_RESPONSE_ACTION} and must not be empty.
 */
public abstract class AsyncService extends android.app.Service {

    public static final String JOB_ID = "job_id";
    public static final String JOB_RESULT = "job_result";
    public static final String JOB_PIGGYBACK = "job_piggyback";
    public static final String JOB_RESPONSE_ACTION = "job_response_action";

    protected final IntentDispatcher mIntentDispatcher = new IntentDispatcher();

    private ThreadWorker mThreadWorker = null;

    /**
     * Call this function to create intents for response.
     * It's will set the job id, piggyback and categories automatically.
     * @param origin
     * @return
     */
    public static Intent createResponseIntent(final Intent origin) {
        final String action = origin.getStringExtra(JOB_RESPONSE_ACTION);
        if (action == null || action.length() <= 0) {
            return null;
        }

        final Intent resp = new Intent(action);

        if (origin.hasExtra(JOB_PIGGYBACK)) {
            final Bundle piggyback = origin.getBundleExtra(JOB_PIGGYBACK);
            resp.putExtra(JOB_PIGGYBACK, piggyback);
        }

        if (origin.hasExtra(JOB_ID)) {
            final String jobId = origin.getStringExtra(JOB_ID);
            resp.putExtra(JOB_ID, jobId);
        }

        return resp;
    }

    /**
     * ************************************************************************
     * Events handlers.
     */

    public Object popMessage() {
    	return mThreadWorker.popMessage();
    }
    
    public void postMessage(Object message) {
    	mThreadWorker.postMessage(message);
    }
    
    /**
     * This method creates a thread worker, and start it.
     * Subclass should register {@link IntentHandler}s before calling this method.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        mThreadWorker = new ThreadWorker();
        mThreadWorker.start();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        handleCommand(intent, startId);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        handleCommand(intent, 0);
        return null;
    }
      
    protected Intent filter(Intent intent) {
    	return intent;
    }

    private void handleCommand(final Intent intent, final int startId) {
        if (intent == null)
            return;

        final Intent filtered = filter(intent);
        if (filtered != null) {
        	Runnable job = new Runnable() {
				@Override
				public void run() {
					if (!mIntentDispatcher.handle(filtered, AsyncService.this, this)) {
		                Log.w("Intent(" + filtered + ") not handled.");
					}
				}
        	};
            //Log.i("Intent received:" + action);
            mThreadWorker.pushJob(job);
        }
    }
}
