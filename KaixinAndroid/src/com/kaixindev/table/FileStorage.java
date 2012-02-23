package com.kaixindev.table;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import com.kaixindev.android.Log;

/**
 * TableJSONFileStorage reads from or writes to the file in the file system with JSON formatted data.
 * @author Wilbur Luo
 */
public class FileStorage implements Storage {
    
    public static final int READONLY = 1;
    
    public static final int DEFAULT_FLAGS = 0;
    
    /**
     * ************************************************************************
     * Properties
     */

	private File mFile = null;
	
	private int mFlags = 0;
	
	private Serializer mSerializer = null; 
	
	/**
     * ************************************************************************
     */
	
	/**
	 * @param file
	 */
	protected FileStorage(Serializer serializer, File file, int flags) {
		mFile = file;
		mFlags = flags;
		mSerializer = serializer;
	}
	
	/**
	 * Create a TableJSONFileStorage.
	 * @param serializer
	 * @param file Which to read or write data.
	 * @return Returns null if fail to create a instance.
	 */
	public static FileStorage newInstance(Serializer serializer, File file) {
		return newInstance(serializer,file,DEFAULT_FLAGS);
	}
	
	/**
	 * Create a TableJSONFileStorage.
	 * @param serializer
	 * @param file
	 * @param flags
	 * @return Returns null if fail to create a instance.
	 */
	public static FileStorage newInstance(Serializer serializer, File file, int flags) {
        if (file == null) {
            return null;
        }
        return new FileStorage(serializer,file,flags);
	}

	@Override
	synchronized public boolean update(Map<String, Value> data) {
		// Write the data to file.
		OutputStream os = null;
		if (!mFile.exists()) {
			File parent = mFile.getParentFile();
			if (!parent.exists() && !parent.mkdirs()) {
				Log.w("Can't create parent directories of file " + parent.getAbsolutePath(),Log.TAG);
				return false;
			}
		}
		
		try {
			os = new FileOutputStream(mFile);
		} catch (FileNotFoundException e) {
			Log.w("Can't write data to file '" + mFile.getAbsolutePath() + "'.",Log.TAG);
			return false;
		}
		
		try {
		    byte[] byteData = mSerializer.serialize(data);
			os.write(byteData);
			Log.i("Write data into '" + mFile.getAbsolutePath() + "'",Log.TAG);
			Log.i(new String(byteData),Log.TAG);
		} catch (IOException e) {
			Log.w("Can't write data to file '" + mFile.getAbsolutePath() + "'.",Log.TAG);
			return false;
		} finally {
			try {
				os.close();
			} catch (IOException e) {
			}
		}

		return true;		
	}

	@Override
	synchronized public Map<String, Value> getData() {
		Log.i("Load data from '" + mFile.getAbsolutePath() + "'",Log.TAG);
		InputStream is = null;
		try {
			is = new FileInputStream(mFile);
		} catch (FileNotFoundException e) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		byte[] buf = new byte[1024];
		int len = 0;
		try {
			while ((len = is.read(buf)) > 0) {
				sb.append(new String(buf,0,len));
			}
		} catch (IOException e) {
			Log.w("Can't read file " + mFile.getAbsolutePath());
			return null;
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				Log.w("Can't close file " + mFile.getAbsolutePath());
			}
		}
		
		return mSerializer.unserialize(sb.toString().getBytes());
	}

	public int getFlags() {
	    return mFlags;
	}
	
	public void setFlags(int flags) {
	    mFlags = flags;
	}
}
