package com.kaixindev.android;

import android.content.Context;
import android.net.wifi.WifiManager;

class WifiHelper {
    public static boolean getWifiEnabled(final Context context) {
        final WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        return wm.isWifiEnabled();
    }

    public static void setEnabled(final Context context, final boolean isEnabled) {
        final WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        wm.setWifiEnabled(isEnabled);
    }
}