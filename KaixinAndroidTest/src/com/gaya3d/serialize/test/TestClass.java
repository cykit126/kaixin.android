package com.gaya3d.serialize.test;

import com.gaya3d.serialize.Attribute;

public class TestClass {
    public int id = 1;
    
    @Attribute
    public String string = "string";
    
    @Attribute
    public Integer[] array = new Integer[] {1,2,3,4};
    
    public TestClass() {
    }
    
    @Override
    public boolean equals(Object another) {
        if (another == null) {
            return false;
        }
        try {
            TestClass a = (TestClass)another;
            if (array.length != a.array.length) {
                return false;
            }
            for (int i=0; i<array.length; ++i) {
                if (array[i] != a.array[i]) {
                    return false;
                }
            }
            return (id == a.id) && string.equals(a.string);
        } catch (Exception e) {
            return false;
        }
    }
}
