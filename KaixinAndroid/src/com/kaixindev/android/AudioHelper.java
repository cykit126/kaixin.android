package com.kaixindev.android;

import android.content.Context;
import android.media.AudioManager;

class AudioHelper {
    public static final String MODE_SILENT = "silent";
    public static final String MODE_VIBRATE = "vibrate";
    public static final String MODE_NORMAL = "normal";

    public static void setSilentMode(final Context context, final String strMode) {
        final AudioManager am = (AudioManager)
                context.getSystemService(Context.AUDIO_SERVICE);

        if (strMode.equals(MODE_SILENT)) {
            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }
        else if (strMode.equals(MODE_VIBRATE)) {
            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }
        else {
            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }
    }

    public static String getSilentMode(final Context context) {
        final AudioManager am = (AudioManager)
                context.getSystemService(Context.AUDIO_SERVICE);
        final int mode = am.getRingerMode();
        switch (mode) {
        case AudioManager.RINGER_MODE_SILENT:
            return MODE_SILENT;
        case AudioManager.RINGER_MODE_VIBRATE:
            return MODE_VIBRATE;
        case AudioManager.RINGER_MODE_NORMAL:
        default:
            return MODE_NORMAL;
        }
    }
}