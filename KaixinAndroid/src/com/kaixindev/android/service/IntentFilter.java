package com.kaixindev.android.service;

import android.content.Intent;

public interface IntentFilter {
    /**
     * Filter intent.
     * @param intent
     * @return Returns true if the specified intent is accepted, otherwise returns false.
     */
    public boolean filter(Intent intent);
}
