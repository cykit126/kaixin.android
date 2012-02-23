package com.kaixindev.core;

import java.util.Formatter;
import java.util.Locale;

public class StringUtil {
    public static boolean isEmpty(final String s) {
        return s == null || s.length() <= 0;
    }
    
    public static String toHex(byte[] b) {
    	StringBuilder sb = new StringBuilder();
    	Formatter fmt = new Formatter(sb, Locale.US);
    	for (int i=0; i<b.length; ++i) {
    		fmt.format("%02X", b[i]);
    	}
    	return fmt.toString();
    }
}
