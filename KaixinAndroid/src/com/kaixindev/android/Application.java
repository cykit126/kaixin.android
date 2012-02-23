package com.kaixindev.android;

import java.io.File;

import com.kaixindev.core.StringUtil;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Application utility, such as installation, uninstallation and so on.
 * @author Wilbur Luo
 */
public class Application {
	
	public static Intent buildInstallIntent(Context context, String path) {
        if (context == null) {
        	return null;
        }
        if (StringUtil.isEmpty(path)) {
        	return null;
        }

        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
        return intent;
	}

	/**
	 * Install application from APK.
	 * @param context
	 * @param path Path of APK
	 * @return true for success, false for failure.
	 */
    public static boolean install(Context context, String path) {
    	Intent intent = buildInstallIntent(context, path);
    	if (intent != null) {
    		context.startActivity(intent);
    		return true;
    	} else {
    		return false;
    	}
    }

    /**
     * Uninstall application by package name.
     * @param context
     * @param packageName Name of application to uninstall.
     * @return true for success, false for failure.
     */
    public static boolean uninstall(Context context,String packageName) {
        if (context == null) return false;
        if (packageName == null || packageName.length() == 0) return false;

        Uri packageURI = Uri.parse("package:" + packageName);
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        context.startActivity(uninstallIntent);
        
        return true;
    }    

}




