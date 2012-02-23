package com.gaya3d.android.io.test;

import java.io.File;

import com.gaya3d.android.FileSystem;
import com.gaya3d.android.Log;

import android.test.AndroidTestCase;
import android.os.Environment;

public class FileSystemTest extends AndroidTestCase {
	@SuppressWarnings("unused")
	private boolean isExternalStorageAvailable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	public void testGetExternalFile() {
		File file = FileSystem.getExternalFile(getContext(), "test", "test.txt");
		assertNotNull(file);
		Log.i(file.getAbsolutePath());
	}
	
	public void testGetExternalCacheFile() {
		File file = FileSystem.getExternalFile(getContext(), "test", "test.txt");
		assertNotNull(file);
		Log.i(file.getAbsolutePath());
	}
	
	public void testGetInternalFile() {
		File file = FileSystem.getInternalFile(getContext(), "test", "test.txt");
		assertNotNull(file);
		Log.i(file.getAbsolutePath());
	}
	
	public void testGetInternalCacheFile() {
		File file = FileSystem.getInternalCacheFile(getContext(), "test", "test.txt");
		assertNotNull(file);
		Log.i(file.getAbsolutePath());
	}
}
