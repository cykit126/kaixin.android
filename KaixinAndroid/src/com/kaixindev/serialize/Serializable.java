package com.kaixindev.serialize;

import java.util.Set;

/**
 * <pre>
 * Classes implements Serializable can be serialize by {@link com.kaixindev.serialize.Serializer Serializer}.
 * 
 * NOTICE:
 * All the classes implements default constructors, so that {@link com.kaixindev.serialize.Serializer Serializer}
 * can use Class.newInstance() to create an instance.
 * </pre>
 * @author Wilbur Luo
 */
public interface Serializable {
    
    /**
     * Get a field by name.
     * @return 
     */
    public Object getField(String key);
    
    /**
     * Get a set of all keys.
     * @return
     */
    public Set<String> keySet();
    
    /**
     * Set fields.
     * @param field
     * @param value
     */
    public void setField(String field, Object value);
}
