package com.kaixindev.table;

import java.util.Map;

/**
 * The storage for KeyValue storage interface.
 * @author Wilbur Luo
 */
public interface Storage {
	/**
	 * Update the data.
	 * @param data
	 * @return
	 */
	public boolean update(Map<String,Value> data);

	/**
	 * Get the data.
	 * @return The data.
	 * If it's empty or can't read from the underlying media, return null.
	 */
	public Map<String,Value> getData();
}
