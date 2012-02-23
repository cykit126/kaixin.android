package com.kaixindev.core;

import java.util.ArrayList;
import java.util.List;

import android.os.ConditionVariable;

import com.kaixindev.android.Log;

/**
 * A ThreadWroker is a thread wait on its jobs queue, get and run the job in its thread.
 * @author Wilbur Luo
 */
public class ThreadWorker {

    private static final String THREAD_NAME = "Thread Worker";
    private static final int END_TIMEOUT = 1000;

    private final List<Object> mMessages = new ArrayList<Object>();
    private final List<Runnable> mJobsQueue = new ArrayList<Runnable>();
    private Thread mThread;
    private final ConditionVariable mReadyEvent = new ConditionVariable(false);
    private final ConditionVariable mActiveEvent = new ConditionVariable(false);
    private final ConditionVariable mQuitEvent = new ConditionVariable(false);
    private Boolean mQuit = false;

    @Override
    public void finalize() {
        synchronized (mQuit) {
            mQuit = true;
        }
        mActiveEvent.open();
        mQuitEvent.block(END_TIMEOUT);
    }

    private class ThreadRunnable implements Runnable {
        @Override
        public void run() {
            mReadyEvent.open();
            while (true) {
                //Log.d("ThreadWorker is waiting for jobs.",Log.TAG);
                mActiveEvent.block();
                mActiveEvent.close();
                synchronized (mQuit) {
                    if (mQuit)
                        break;
                }
                //Log.d("ThreadWorker is active.",Log.TAG);
                while (true) {
                    final Runnable job = popJob();
                    if (job == null) {
                        Log.d("No job.", Log.TAG);
                        break;
                    }
                    //Log.d("ThreadWorker is processing job.",Log.TAG);
                    job.run();
                }
            }
            mQuitEvent.open();
            Log.w("ThreadWorker quit.");
        }
    }

    /**
     * Start this ThreadWorker, this function create a thread, and return when the thread is ready
     * for receiving jobs.
     * @return
     */
    public boolean start() {
        final ThreadRunnable tr = new ThreadRunnable();
        mThread = new Thread(tr, THREAD_NAME);
        mThread.start();
        mReadyEvent.block();
        return true;
    }
    
    public void postMessage(Object message) {
    	synchronized (mMessages) {
    		mMessages.add(mMessages.size(), message);
		}
    }
    
    public Object popMessage() {
    	synchronized (mMessages) {
    		if (!mMessages.isEmpty()) {
    			Object message = mMessages.get(0);
    			mMessages.remove(0);
    			return message;
    		} else {
    			return null;
    		}
		}
    }

    /**
     * Push a job into the job queue.
     * @param job
     */
    public void pushJob(final Runnable job) {
        synchronized (mJobsQueue) {
            mJobsQueue.add(mJobsQueue.size(), job);
        }
        mActiveEvent.open();
    }

    private Runnable popJob() {
        synchronized (mJobsQueue) {
            if (!mJobsQueue.isEmpty()) {
                final Runnable job = mJobsQueue.get(0);
                mJobsQueue.remove(0);
                return job;
            }
            else {
                return null;
            }
        }
    }
    
}