package com.kaixindev.table;

import com.kaixindev.serialize.Attribute;

/**
 * This is the representation of values that can be stored in a Table.
 * Every TableValue contains a string and a version.
 * @author Wilbur Luo
 */
public class Value {
    @Attribute
    public long version = 0;
    
    @Attribute
    public Object data = null;
    
    public Value() {
    }

    /**
     * Constructor.
     * @param data
     * @param version
     */
    public Value(Object data,long version) {
        this.data = data;
        this.version = version;
    }
    
    /**
     * Check if this object is with another one.
     * @param o
     * @return true for yes, false for no.
     */
    public boolean equals(Value o) {
    	if (o != null) {
    		return (data.equals(o.data)) && (version==o.version);
    	} else {
    		return false;
    	}
    }
    
    /**
     * String representation of this object.
     */
    public String toString() {
    	return "{data=" + data.toString() + ", version=" + version + "}";
    }
}
