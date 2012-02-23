package com.kaixindev.android.service;

import android.content.Intent;

public class AsyncIntent extends Intent {

    private Intent mRespIntent = null;

    public AsyncIntent(final Intent respIntent) {
        mRespIntent = respIntent;
    }

    public AsyncIntent() {
    }

    public Intent getResponseIntent() {
        return mRespIntent;
    }
}
