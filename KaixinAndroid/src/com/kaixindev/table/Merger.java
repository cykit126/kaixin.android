package com.kaixindev.table;

import java.util.Map;

public class Merger {
    /**
     * Merge Map right into Map left. 
     * @param left Map to be merged.
     * @param right
     * @return Returns true if left is updated, false for not.
     */
    public static boolean merge(Map<String,Value> left, Map<String,Value> right) {
        boolean changed = false;
        for (String key : right.keySet()) {
            if (left.containsKey(key)) {
                Value leftValue = left.get(key);
                Value rightValue = right.get(key);
                if (leftValue.version < rightValue.version) {
                    left.put(key, rightValue);
                    changed = true;
                }
            } else {
                left.put(key, right.get(key));
                changed = true;
            }
        }
        return changed;
    }
}
