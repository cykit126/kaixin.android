package com.kaixindev.table;

import java.util.Map;

/**
 * TableJSONFileStorage reads from or writes to a string with JSON formatted data.
 * @author Wilbur Luo
 */
public class StringStorage implements Storage {
	
	private String mData = null;
	
	private Serializer mSerializer = null;
	
	public StringStorage(Serializer serializer,String data) {
		mData = data;
		mSerializer = serializer;
	}

	public StringStorage(Serializer serializer) {
	    mSerializer = serializer;
    }

    @Override
	synchronized public boolean update(Map<String, Value> data) {
		mData = new String(mSerializer.serialize(data));
		return true;
	}

	@Override
	synchronized public Map<String, Value> getData() {
		return mSerializer.unserialize(mData.getBytes());
	}

	/**
	 * Get the underlying string.
	 * @return
	 */
	synchronized public String getStringData() {
		return mData;
	}
	
	synchronized public void setStringData(String data) {
		mData = data;
	}
}
