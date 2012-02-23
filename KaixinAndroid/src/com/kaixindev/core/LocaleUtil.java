package com.kaixindev.core;

import java.util.Locale;

/**
 * A locale utility class.
 * @author Wilbur Luo
 */
public class LocaleUtil {
    
    /**
     * Create locale from string such as zh, zh_CN, and en_US_POSIX.
     * Refer to {@link java.util.Locale} for more information.
     * @param locale
     * @return Return null for failure.
     */
    public static Locale fromString(String locale) {
        if (locale != null && locale.length() > 0) {
            String[] segments = parseSegments(locale);
            return new Locale(segments[0],segments[1],segments[2]);
        } else {
            return null;
        }
    }
    
    /**
     * Parse segments of locale, including language, country and variant.
     * @param locale
     * @return {language,country,variant}
     */
    public static String[] parseSegments(String locale) {
        String lang = null;
        String country = null;
        String variant = null;
        
        if (locale != null && locale.length() > 0) {
            String[] segments = locale.split("_");
            if (segments.length > 0) {
                lang = segments[0];
            }
            if (segments.length > 1) {
                country = segments[1];
            }
            if (segments.length > 2) {
                variant = segments[2];
            }
        }
        
        return new String[] {lang,country,variant};
    }
}
