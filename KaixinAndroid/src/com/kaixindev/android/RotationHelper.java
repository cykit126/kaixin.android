package com.kaixindev.android;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

class RotationHelper {
    public static final String ROTATION_0 = "ROTATION_0";
    public static final String ROTATION_90 = "ROTATION_90";
    public static final String ROTATION_180 = "ROTATION_180";
    public static final String ROTATION_270 = "ROTATION_270";

    public static void setAutoRotationEnabled(final Context context, final boolean isEnabled)
    {
        final ContentResolver cr = context.getContentResolver();
        Settings.System.putInt(cr, Settings.System.ACCELEROMETER_ROTATION, isEnabled ? 1 : 0);
    }

    public static boolean getAutoRotationEnabled(final Context context) {
        final ContentResolver cr = context.getContentResolver();
        try {
            final int isEnabled = Settings.System.getInt(cr, Settings.System.ACCELEROMETER_ROTATION);
            return isEnabled != 0;
        }
        catch (final Exception e) {
            Log.e("Error:" + e.getMessage(),Log.TAG);
            return false;
        }
    }

    public static String getRotation(final Context context) {
        final WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();
        switch (display.getRotation()) {
        case Surface.ROTATION_90:
            return ROTATION_90;
        case Surface.ROTATION_180:
            return ROTATION_180;
        case Surface.ROTATION_270:
            return ROTATION_270;
        case Surface.ROTATION_0:
        default:
            return ROTATION_0;
        }
    }
}