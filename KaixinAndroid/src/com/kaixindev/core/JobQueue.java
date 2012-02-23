package com.kaixindev.core;

import java.util.ArrayList;
import java.util.List;

public class JobQueue {
    private final List<Runnable> mJobs = new ArrayList<Runnable>();

    public void push(final Runnable r) {
        synchronized (mJobs) {
            mJobs.add(mJobs.size(), r);
        }
    }

    public Runnable pop() {
        synchronized (mJobs) {
            if (!mJobs.isEmpty()) {
                return mJobs.remove(0);
            }
            else {
                return null;
            }
        }
    }

    public void run() {
        Runnable job;
        while ((job = pop()) != null) {
            job.run();
        }
    }
}
