package com.kaixindev.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.kaixindev.serialize.Serializable;

/**
 * @author Wilbur Luo
 */
public class I18NString implements Serializable {
    
    private Map<String,String> mLocaleStringMap = new HashMap<String,String>();
    
    /**
     * Get value for the specified locale.
     * e.g. Get value for en_US_POSIX. 
     * If en_US_POSIX exists, return value for en_US_POSIX, otherwise
     * if en_US exists, return value for en_US, otherwise
     * if en exists, return value for en, otherwise
     * return null.
     * @param locale
     * @return Return null if not found.
     */
    public String get(String locale) {
        if (locale == null || locale.length() <= 0) {
            return null;
        }
        
        String[] segments = LocaleUtil.parseSegments(locale);
        String lang = segments[0];
        String country = segments[1];
        String variant = segments[2];
        
        String key2 = lang + "_" + country;
        String key3 = lang + "_" + country + "_" + variant;
        
        if (mLocaleStringMap.containsKey(key3)) {
            return mLocaleStringMap.get(key3);
        } else if (mLocaleStringMap.containsKey(key2)) {
            return mLocaleStringMap.get(key2);
        } else if (mLocaleStringMap.containsKey(lang)) {
            return mLocaleStringMap.get(lang);
        } else {
            return null;
        }
    }
    
    /**
     * Get exact value for the specified locale.
     * @param locale
     * @return Return null if not found.
     */
    public String getExactValue(String locale) {
        return mLocaleStringMap.get(locale);
    }
    
    /**
     * Set value for the specified locale.
     * @param locale
     * @param value
     */
    public void put(String locale, String value) {
        if (locale == null || locale.length() <= 0) {
            return;
        }
        mLocaleStringMap.put(locale,value);
    }
    
    /**
     * Returns languages.
     * @return
     */
    public Set<String> keySet() {
        return mLocaleStringMap.keySet();
    }

    @Override
    public void setField(String field, Object value) {
        try {
            mLocaleStringMap.put(field,(String)value);
        } catch (Exception e) {}
    }

    @Override
    public Object getField(String key) {
        return mLocaleStringMap.get(key);
    }
}
