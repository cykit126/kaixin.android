package com.kaixindev.android;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

class APNHelper {
    //private static final String CURRENT_APN_URI = "content://telephony/carriers/preferapn";

    public static final String TAG = "APNUtils";
    public static boolean mLastState = false;

    public static void setEnabled(final Activity context, final boolean enabled) {
        final TelephonyManager telephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);

        final Class<TelephonyManager> telephonyManagerClass = TelephonyManager.class;
        Method getITelephonyMethod = null;
        try {
            getITelephonyMethod = telephonyManagerClass.getDeclaredMethod("getITelephony");
        }
        catch (final NoSuchMethodException e) {
            Log.e("Error:" + e.getMessage(),Log.TAG);
        }
        getITelephonyMethod.setAccessible(true);

        Object ITelephonyStub = null;
        Class<?> ITelephonyClass;
        try {
            ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);
            ITelephonyClass = Class.forName(ITelephonyStub.getClass().getName());

            Method dataConnSwitchmethod;
            if (enabled) {
                dataConnSwitchmethod = ITelephonyClass.getDeclaredMethod("enableDataConnectivity");
            }
            else {
                dataConnSwitchmethod = ITelephonyClass.getDeclaredMethod("disableDataConnectivity");
            }
            dataConnSwitchmethod.setAccessible(true);
            dataConnSwitchmethod.invoke(ITelephonyStub);

            final ContentResolver cr = context.getContentResolver();
            if (enabled) {
                Settings.Secure.putInt(cr, "mobile_data", 1);
            }
            else {
                Settings.Secure.putInt(cr, "mobile_data", 0);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            Log.e("Error:" + e.getMessage());
        }
    }

    public static boolean getDefaultState(final Context context) {
        final ContentResolver cr = context.getContentResolver();
        final int mobileData = Settings.Secure.getInt(cr, "mobile_data", 0);
        return mobileData != 0;
    }

}