package com.kaixindev.android;

import android.os.Environment;

public class OSUtil {
    public static boolean isExternalStorageMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
