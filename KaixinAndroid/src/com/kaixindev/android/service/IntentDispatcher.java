package com.kaixindev.android.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.kaixindev.android.Log;

/**
 * When work with android Activity or Service, we have to handle intents.
 * Usually, we check the intent's action and dispatch them with a lot of if-else statements.
 * This is not good, because we have to put everything in one big function.
 * IntentHandler and IntentComsumer are designed to solve this problem. 
 * We associate handlers with actions' name. Intents are dispatched to the associated handler automatically.
 * @author Wilbur Luo
 */
public class IntentDispatcher {

    /**
     * Filters
     */
    private final List<IntentFilter> mFilters = new ArrayList<IntentFilter>();

    /**
     * Clear filters.
     */
    public void clearFilters() {
        mFilters.clear();
    }

    /**
     * Add a filter if it's not in the list. 
     * @param filter
     */
    public void addFilter(final IntentFilter filter) {
        if (!mFilters.contains(filter)) {
            mFilters.add(filter);
        }
    }

    /**
     * Remove a filter
     */
    public void removeFilter(final IntentFilter filter) {
        mFilters.remove(filter);
    }

    /**
     * Intent handler interface. 
     * Classes implement this interface can be registered with a intent action anme.
     * @author Wilbur Luo
     */
    public interface Handler {
        public void handle(Intent intent, Context context, Object extra);
    }

    private abstract class HandlerProxy implements Handler {
        Object mObj;

        HandlerProxy(final Object obj) {
            mObj = obj;
        }
    }

    /**
     * Map of actions and handlers
     */
    private final HashMap<String,Handler> mHandlers = new HashMap<String,Handler>();

    /**
     * Clear handlers.
     */
    public void clear() {
        mHandlers.clear();
    }

    /**
     * Register handler with action name.
     * @param action Name of action.
     * @param handler Handler to be registered.
     * @return true for success, false for failure.
     */
    public boolean registerHandler(final String action, final Handler handler) {
        if (action == null || action.length() == 0) {
            Log.e("action is empty.", Log.TAG);
            return false;
        }
        if (handler == null) {
            Log.e("handle is null.", Log.TAG);
            return false;
        }
        mHandlers.put(action, handler);
        return true;
    }

    /**
     * Register handlers in a class with IntentHandler annotations.
     * @param object
     */
    public void registerHandlers(final Object object) {
        try {
            final Class<?> klass = object.getClass();
            for (final Method m : klass.getDeclaredMethods()) {
                final IntentHandler ih = m.getAnnotation(IntentHandler.class);
                if (ih != null) {
                    final Handler handler = new HandlerProxy(object) {
                        @Override
                        public void handle(final Intent intent, final Context context, final Object extra)
                        {
                            final Object[] args = new Object[] {
                                    intent,
                                    context,
                                    extra
                            };
                            try {
                                m.invoke(mObj, args);
                            }
                            catch (final Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    Log.i("Register handler, action: " + ih.action() + ", method: " + klass.getName() + "." + m.getName(), Log.TAG);
                    registerHandler(ih.action(), handler);
                }
            }
        }
        catch (final Exception e) {
            Log.w(e.getMessage(), Log.TAG);
        }
    }

    /**
     * Call this function to dispatch a intent.
     * @param intent
     * @return If dispatched, return true. Returns false if no handler found.
     */
    public boolean handle(final Intent intent, final Context context, final Object extra)
    {
        if (intent == null) {
            Log.w("intent is null.", Log.TAG);
            return false;
        }

        // Filter intent.
        for (final IntentFilter filter : mFilters) {
            if (!filter.filter(intent)) {
                Log.i("Discard " + intent.toString());
                return true;
            }
        }

        // Dispatch intent.
        final String action = intent.getAction();
        if (action == null || action.length() == 0) {
            Log.w("action is empty.", Log.TAG);
            return false;
        }
        final Handler handler = mHandlers.get(action);
        if (handler != null) {
            handler.handle(intent, context, extra);
            return true;
        }
        else {
            return false;
        }
    }

}
