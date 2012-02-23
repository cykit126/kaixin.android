package com.kaixindev.core;

public class ObjectUtil {

    public static boolean equals(final Object o1, final Object o2) {
        if (o1 == null && o2 == null) {
            return true;
        }
        else if (o1 != null) {
            return o1.equals(o2);
        }
        else {
            return o2.equals(o1);
        }
    }
}
