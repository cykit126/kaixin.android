package com.gaya3d.serialize.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.gaya3d.serialize.Serializable;

public class SerializableTestClass implements Serializable {
    public final Map<String,Object> mData = new HashMap<String,Object>();
    
    public SerializableTestClass() {
    }
    
    @Override
    public boolean equals(Object another) {
        if (another == null) {
            return false;
        }
        try {
            SerializableTestClass a = (SerializableTestClass)another;
            return mData.equals(a.mData);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void setField(String field, Object value) {
        mData.put(field, value);
    }

    @Override
    public Object getField(String key) {
        return mData.get(key);
    }

    @Override
    public Set<String> keySet() {
        return mData.keySet();
    }
}
