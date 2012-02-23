package com.kaixindev.android;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

class GPSHelper {
    public static void setEnabled(final Context context, final boolean isEnabled) {
        final String provider = Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (isEnabled && !provider.contains("gps")) {
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            context.sendBroadcast(poke);
        }

        if (!isEnabled && provider.contains("gps")) {
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            context.sendBroadcast(poke);
        }
    }

    public static boolean getEnabled(final Context context) {
        final String provider = Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        return provider.contains("gps");
    }
}