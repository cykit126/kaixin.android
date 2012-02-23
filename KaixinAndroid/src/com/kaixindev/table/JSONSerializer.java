package com.kaixindev.table;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONSerializer implements Serializer {
	private static final String KEY_DATA = "data";
    private static final String KEY_VERSION = "version";
    
    /**
     * Parse data form JSON string.
     * @param value JSON string.
     * @return true on success, false on failure.
     */
	public Map<String,Value> unserialize(byte[] value) {
	    if (value == null || value.length <= 0) {
	        return null;
	    }
	    
		try {
			JSONObject obj = new JSONObject(new String(value));
			Map<String,Value> data = new HashMap<String,Value>();
			@SuppressWarnings("unchecked")
			Iterator<String> keys = obj.keys();
			while (keys.hasNext()) {
				String key = keys.next();
				data.put(key,fromJSONObject(obj.optJSONObject(key)));
			}
			return data;
		} catch (JSONException e) {
		    e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Convert to JSON string.
	 */
	public byte[] serialize(Map<String,Value> data) {
		JSONObject obj = new JSONObject();
		for (String key : data.keySet()) {
			try {
				obj.put(key, toJSONObject(data.get(key)));
			} catch (JSONException e) {
			}
		}
		try {
			return obj.toString(4).getBytes();
		} catch (JSONException e) {
			return "{}".getBytes();
		}
	}
	
	/**
	 * Convert JSON object to Value.
	 * @param obj
	 * @return
	 */
    public static Value fromJSONObject(JSONObject obj) {
        if (obj != null) {
            return new Value(obj.opt(KEY_DATA),obj.optLong(KEY_VERSION,0));
        } else {
            return null;
        }
    }
    
	/**
	 * Convert Value to JSONObject.
	 * @param value
	 * @return
	 */
    public static JSONObject toJSONObject(Value value) {
        if (value == null) {
            return null;
        } else {
            JSONObject obj = new JSONObject();
            try {
                obj.put(KEY_VERSION, value.version);
                obj.put(KEY_DATA, value.data);
            }
            catch (JSONException e) {}
            return obj;
        }
    }
}
