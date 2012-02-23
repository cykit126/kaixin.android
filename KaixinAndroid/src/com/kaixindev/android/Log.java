package com.kaixindev.android;

import java.util.Formatter;
import java.util.Locale;

public class Log {
    public static String TAG = "com.gaya3d.android";
    public static boolean ENABLED = true;

    public static void v(final String message, final String tag) {
        if (ENABLED) {
            final String msg = formatMessage(filterMessage(message), getStackTraceElement());
            android.util.Log.v(tag, msg);
        }
    }

    public static void v(final String message) {
        v(message, TAG);
    }

    public static void d(final String message, final String tag) {
        if (ENABLED) {
            final String msg = formatMessage(filterMessage(message), getStackTraceElement());
            android.util.Log.d(tag, msg);
        }
    }

    public static void d(final String message) {
        d(message, TAG);
    }

    public static void i(final String message, final String tag) {
        if (ENABLED) {
            final String msg = formatMessage(filterMessage(message), getStackTraceElement());
            android.util.Log.i(tag, msg);
        }
    }

    public static void i(final String message) {
        i(message, TAG);
    }

    public static void w(final String message, final String tag) {
        if (ENABLED) {
            final String msg = formatMessage(filterMessage(message), getStackTraceElement());
            android.util.Log.w(tag, msg);
        }
    }

    public static void w(final String message) {
        w(message, TAG);
    }

    public static void e(final String message, final String tag) {
        if (ENABLED) {
            final String msg = formatMessage(filterMessage(message), getStackTraceElement());
            android.util.Log.e(tag, msg);
        }
    }

    public static void e(final String message) {
        e(message, TAG);
    }

    private static String filterMessage(final String message) {
        return message != null ? message : "";
    }

    private static String formatMessage(final String msg, final StackTraceElement stackTraceElement)
    {
        if (stackTraceElement != null) {
            final StringBuilder sb = new StringBuilder();
            final Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format("%s -- %s:%d", msg, stackTraceElement.getFileName(), stackTraceElement.getLineNumber());
            return sb.toString();
        }
        else {
            return msg;
        }
    }

    private static StackTraceElement getStackTraceElement() {
        final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if (stackTraceElements == null || stackTraceElements.length == 0) {
            return null;
        }
        for (int i = stackTraceElements.length - 1; i >= 0; --i) {
            final StackTraceElement el = stackTraceElements[i];
            if (el.getClassName().equals(Log.class.getName())) {
                if (i + 1 < stackTraceElements.length) {
                    return stackTraceElements[i + 1];
                }
                else {
                    return null;
                }
            }
        }
        return null;
    }
}
