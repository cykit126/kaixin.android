package com.kaixindev.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kaixindev.core.BitsOperation;


/**
 * This class is designed as a key-value storage, 
 * which can synchronize data between different media, 
 * such as SharedPreferences, XML file, JSON file, WebService and so on.
 * @notice All keys can't contains '@'.
 * @author Wilbur Luo
 */
public class Table {
    public static final int FLUSH_IMMEDIATELY = 1;
    
    public static final int READONLY = 1 << 1;
    
    public static final int DEFAULT_FLAGS = 0;
    
	/**
	 * Successfully synchronized.
	 */
	public static final int SYNCHRONIZED = 1;
	
	/**
	 * Can't read data from storage.
	 */
	public static final int CANT_READ = 2;
	
	/**
	 * Can't write data to storage.
	 */
	public static final int CANT_WRITE = 3;

	/**
	 * Private members.
	 */
    private final HashMap<String,Value> mData = new HashMap<String,Value>();
    private final List<Storage> mStorages = new ArrayList<Storage>();
    private int mFlags = 0;

    /**
     * Initialization with DEFAULT_FLAGS.
     */
    public Table() {
       setFlags(DEFAULT_FLAGS);
    }

    /**
     * Initialization with flags
     * @param flags
     */
    public Table(final int flags) {
        setFlags(flags);
    }

    /**
     * Add new storage to the current instance.
     * @param storage
     */
    synchronized public void addStorage(Storage storage) {
    	if (storage != null && !mStorages.contains(storage)) {
    		mStorages.add(storage);
    	}
    }
    
    /**
     * Get string value by key.
     * @param key 
     * @param defValue If the key doesn't exist, this value is returned.
     * @return Value associated with key.
     */
    synchronized public Object get(String key, Object defValue) {
        if (mData.containsKey(key)) {
            return mData.get(key).data;
        }
        else {
            return defValue;
        }        
    }
    

    /**
     * Get string value by key.
     * @param key
     * @param defValue If the key doesn't exist, this value is returned.
     * @return Value associated with key.
     */
    synchronized public String getString(String key, String defValue) {
        if (mData.containsKey(key)) {
            try {
                return (String)mData.get(key).data;
            } catch (Exception e) {}
        }
        return defValue;
    }
    
    /**
     * Get boolean value by key.
     * @param key
     * @param defValue If the key doesn't exist, this value is returned.
     * @return Value associated with key.
     */    
    synchronized public boolean getBoolean(String key, boolean defValue) {
        if (mData.containsKey(key)) {
            try {
                return (Boolean)mData.get(key).data;
            } catch (Exception e) {}
        }
        return defValue;   
    }
    
    /**
     * Get byte value by key.
     * @param key
     * @param defValue If the key doesn't exist, this value is returned.
     * @return Value associated with key.
     */    
    synchronized public byte getByte(String key, byte defValue) {
        if (mData.containsKey(key)) {
            try {
                return (Byte)mData.get(key).data;
            } catch (Exception e) {}
        }
        return defValue;
    }
    
    /**
     * Get short value by key.
     * @param key
     * @param defValue If the key doesn't exist, this value is returned.
     * @return Value associated with key.
     */    
    synchronized public short getShort(String key, Short defValue) {
        if (mData.containsKey(key)) {
            try {
                return (Short)mData.get(key).data;
            } catch (Exception e) {}
        }
        return defValue;      
    }
    
    /**
     * Get integer value by key.
     * @param key 
     * @param defValue If the key doesn't exist, this value is returned.
     * @return Value associated with key.
     */
    synchronized public int getInt(String key, int defValue) {
        if (mData.containsKey(key)) {
            try {
                return (Integer)mData.get(key).data;
            } catch (Exception e) {}
        }
        return defValue;      
    }
    
    /**
     * Get long value by key.
     * @param key 
     * @param defValue If the key doesn't exist, this value is returned.
     * @return Value associated with key.
     */
    synchronized public long getLong(String key, long defValue) {
        if (mData.containsKey(key)) {
            try {
                return (Long)mData.get(key).data;
            } catch (Exception e) {}
        }
        return defValue;
    }
    
    /**
     * Get float value by key.
     * @param key 
     * @param defValue If the key doesn't exist, this value is returned.
     * @return Value associated with key.
     */
    synchronized public float getFloat(String key, float defValue) {
        if (mData.containsKey(key)) {
            try {
                return (Float)mData.get(key).data;
            } catch (Exception e) {}
        }
        return defValue;
    }
    
    /**
     * Get double value by key.
     * @param key 
     * @param defValue If the key doesn't exist, this value is returned.
     * @return Value associated with key.
     */
    synchronized public double getDouble(String key, double defValue) {
        if (mData.containsKey(key)) {
            try {
                return (Double)mData.get(key).data;
            } catch (Exception e) {}
        }
        return defValue;
    }
    
    /**
     * Set Object value by key.
     * If READONLY is on, this function will fail.
     * @param key
     * @param value
     * @return true for success, false for failure.
     */
    synchronized public boolean set(String key, Object value) {
        if (BitsOperation.isBitsOn(mFlags, READONLY)) {
            return false;
        }
        mData.put(key, new Value(value,System.currentTimeMillis()));
        if (isFlushImmediately()) {
            synchronize();
        }
        return true;
    }
    
    /**
     * Set flags
     * @param mFlags Can be SYNC_IMMEDIATELY.
     */
    synchronized public void setFlags(final int mFlags) {
        this.mFlags = mFlags;
    }

    /**
     * Get flags.
     * @return
     */
    synchronized public int getFlags() {
        return mFlags;
    }
    
    /**
     * Clear all the date, and if FLUSH_IMMEDIATELY is set, 
     * all storages are flushed immediately. 
     * If READONLY is on, this function will fail.
     * @param value
     * @return true for success, false for failure.
     */
    synchronized public boolean clear() {
    	if (BitsOperation.isBitsOn(mFlags, READONLY)) {
    		return false;
    	}
    	mData.clear();
        if (isFlushImmediately()) {
            for (Storage storage : mStorages) {
            	storage.update(mData);
            }
        }
        return true;
    }

    /**
     * Synchronized data from storages.
     */
    synchronized public void synchronize() {
        for (Storage storage : mStorages) {
        	Map<String,Value> data = storage.getData();
        	if (data != null) {
        		Merger.merge(mData, data);
        	}
        }

    }
    
    /**
     * Flush data into storages, regardless of FLUSH_IMMEDIATELY is set or not.
	 * If this function fails, that means at least on of the storages is not flushed.
     * @return true for success, false for failure.
     */
    synchronized public boolean flush() {
        boolean ret = true;
        for (Storage storage : mStorages) {
        	if (!storage.update(mData)) {
        		ret = false;
        	}
        }
        return ret;    	
    }
    
    /**
     * Return all of the data.
     * @return
     */
    synchronized public Map<String,Value> getAll() {
    	return mData;
    }
    
    /**
     * Set all data from Map.
     * @param data
     */
    synchronized public void setAll(Map<String,Value> data) {
    	if (data != null) {
    		if (Merger.merge(mData, data) && isFlushImmediately()) {
    			flush();
    		}
    	}
    }
    
    /**
     * Check is a key is in the table.
     * @param key
     * @return true for yes, false for no.
     */
    synchronized public boolean hasKey(String key) {
        if (key != null && key.length() > 0 && mData != null) {
            return mData.containsKey(key);
        } else {
            return false;
        }
    }
    
    /**
     * Returns a set of keys within this table.
     * @return
     */
    synchronized public Set<String> keySet() {
        if (mData != null) {
            return mData.keySet();
        } else {
            return null;
        }
    }
    
    /**
     * Check if FLUSH_IMMEDIATELY is set.
     * @return
     */
    private boolean isFlushImmediately() {
        return BitsOperation.isBitsOn(mFlags, FLUSH_IMMEDIATELY);
    }
}
