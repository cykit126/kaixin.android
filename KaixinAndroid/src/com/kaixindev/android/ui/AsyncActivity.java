package com.kaixindev.android.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.kaixindev.android.service.IntentDispatcher;

/**
 * AsyncActivity register and unregister {@link android.content.BroadcastReceiver BroadcastReceiver} automatically,
 * which registers itself as a intent handler object. 
 * See {@link com.kaixindev.android.service.IntentHandler IntentHandler} and {@link com.kaixindev.android.service.IntentDispatcher IntentDispatcher}.
 * Abstract method {@link AsyncActivity#getIntentFilter getIntentFilter} must be implemented.
 * @author Wilbur Luo
 */
abstract public class AsyncActivity extends Activity {

    private final IntentDispatcher mIntentDispatcher = new IntentDispatcher();
    private final Receiver mReceiver = new Receiver();

    private class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            mIntentDispatcher.handle(intent, AsyncActivity.this, null);
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntentDispatcher.registerHandlers(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, getIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    abstract protected IntentFilter getIntentFilter();

    public IntentDispatcher getIntentDispatcher() {
        return mIntentDispatcher;
    }
}
