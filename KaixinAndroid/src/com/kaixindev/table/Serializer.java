package com.kaixindev.table;

import java.util.Map;

public interface Serializer {
    /**
     * Unserialize data from string.
     * @param value
     * @return Returns null if failed. 
     */
    public Map<String,Value> unserialize(byte[] value);
    
    /**
     * Serialize data into string.
     * @param data
     * @return String
     */
    public byte[] serialize(Map<String,Value> data);
}
