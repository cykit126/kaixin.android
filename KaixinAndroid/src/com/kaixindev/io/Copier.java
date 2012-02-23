package com.kaixindev.io;

import java.util.ArrayList;
import java.util.List;

public class Copier {
    public interface OnStartListener {
        /**
         * @param input
         * @return return false to abort.
         */
        public boolean onStart(InputBuffer input);
    }

    public interface OnProgressListener {
        /**
         * @param current
         * @param total 
         * @return Return false to abort.
         */
        public boolean onProgress(InputBuffer input, OutputBuffer output, final long current);
    }

    public interface OnEndListener {
        /**
         * 
         * @param input
         * @param output
         * @param size Size copied.
         */
        public void onEnd(InputBuffer input, OutputBuffer output, long size);
    }

    /**
     * Implementation
     */
    private final List<OnProgressListener> mOnProgressListeners = new ArrayList<OnProgressListener>();
    private final List<OnStartListener> mOnStartListeners = new ArrayList<OnStartListener>();
    private final List<OnEndListener> mOnEndListeners = new ArrayList<OnEndListener>();
    private int mNotifyIntervalMillis = 1000;
    private InputBuffer mFrom;
    private OutputBuffer mTo;

    /**
     * These listeners may not be called if copy is aborted or error occurs.
     * @param listener
     */
    public void addOnProgressListener(final OnProgressListener listener) {
        if (listener != null && !mOnProgressListeners.contains(listener)) {
            mOnProgressListeners.add(listener);
        }
    }

    public void clearOnProgressListeners() {
        mOnProgressListeners.clear();
    }

    /**
     * These listeners will always be called. 
     * @param listener
     */
    public void addOnStartListener(final OnStartListener listener) {
        if (listener != null && !mOnStartListeners.contains(listener)) {
            mOnStartListeners.add(listener);
        }
    }

    public void clearOnStartListeners() {
        mOnStartListeners.clear();
    }

    /**
     * These listeners may not be called if copy is aborted or error occurs.
     * @param listener
     */
    public void addOnEndListener(final OnEndListener listener) {
        if (listener != null && !mOnEndListeners.contains(listener)) {
            mOnEndListeners.add(listener);
        }
    }

    public void clearOnEndListeners() {
        mOnEndListeners.clear();
    }

    public void setNotifyInterval(final int millis) {
        if (millis > 0) {
            mNotifyIntervalMillis = millis;
        }
    }

    public Copier reset() {
        clearOnEndListeners();
        clearOnProgressListeners();
        clearOnStartListeners();
        return this;
    }

    public Copier setFrom(final InputBuffer from) {
        mFrom = from;
        return this;
    }

    public Copier setTo(final OutputBuffer to) {
        mTo = to;
        return this;
    }

    /**
     * Copy to the specified file.
     * @param file
     * @return Size copied, -1 for error or aborted.
     */
    public long copy() {
        if (mFrom == null || mTo == null) {
            return -1;
        }
        final long size = doCopy(mFrom, mTo);
        for (final OnEndListener l : mOnEndListeners) {
            l.onEnd(mFrom, mTo, size);
        }
        return size;
    }

    private long doCopy(final InputBuffer input, final OutputBuffer output) {
        for (final OnStartListener l : mOnStartListeners) {
            if (!l.onStart(input)) {
                return -1;
            }
        }

        final int BUFFER_SIZE = 4096;
        long lastNotify = 0;
        long current = 0;
        int len = 0;
        final byte[] buffer = new byte[BUFFER_SIZE];
        while ((len = input.read(buffer, 0, BUFFER_SIZE)) > 0) {
            current += len;
            output.write(buffer, 0, len);
            final long now = System.currentTimeMillis();
            if (now - lastNotify >= mNotifyIntervalMillis) {
                lastNotify = now;
                for (final OnProgressListener l : mOnProgressListeners) {
                    if (!l.onProgress(input, output, current)) {
                        return -1;
                    }
                }
            }
        }
        for (final OnProgressListener l : mOnProgressListeners) {
            if (!l.onProgress(input, output, current)) {
                return -1;
            }
        }

        return current;
    }
}