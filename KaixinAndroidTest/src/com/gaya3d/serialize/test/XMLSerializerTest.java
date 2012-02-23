package com.gaya3d.serialize.test;

import com.gaya3d.android.Log;
import com.gaya3d.serialize.XMLSerializer;
import android.test.AndroidTestCase;

public class XMLSerializerTest extends AndroidTestCase {
    
    public static final String LOG_TAG = "XMLSerializerTest";
    
    public void testPrimitive() {
//        Log.i("boolean:" + boolean[].class.getName(),LOG_TAG);
//        Log.i("byte:" + byte[].class.getName(),LOG_TAG);
//        Log.i("short:" + short[].class.getName(),LOG_TAG);
//        Log.i("int:" + int[].class.getName(),LOG_TAG);
//        Log.i("Long:" + long[].class.getName(),LOG_TAG);
//        Log.i("Float:" + float[].class.getName(),LOG_TAG);
//        Log.i("Double:" + double[].class.getName(),LOG_TAG);
        
        boolean b = false;
        assertEquals(b,doTest(b));
        
        byte by = 10;
        assertEquals(by,doTest(by));
        
        short s = 20;
        assertEquals(s,doTest(s));
        
        int i = 100;
        assertEquals(i,doTest(i));
        
        long l = 200;
        assertEquals(l,doTest(l));
        
        float f = 20.0f;
        assertEquals(f,doTest(f));
        
        double d = 30.3;
        assertEquals(d,doTest(d));
    }
    
    public void testClass() {
        TestClass c = new TestClass();
        assertEquals(c,doTest(c));
    }
 
    public void testArray() {
        Integer[] array = new Integer[] {1,2,3,4};
        XMLSerializer serializer = new XMLSerializer();
        byte[] content = serializer.serialize(array);
        Integer[] array2 = (Integer[]) serializer.unserialize(content);
        assertArray(array,array2);
    }
    
    public void testClassArray() {
        TestClass c = new TestClass();
        TestClass[] array = new TestClass[] {c,c,c,c,c};
        assertArray((Object[])array,(Object[])doTest(array));        
    }
    
    public void testSerializableClass() {
        SerializableTestClass o = new SerializableTestClass();
        o.mData.put("key1",1);
        o.mData.put("key2",2);
        o.mData.put("key3",3);
        assertEquals(o,doTest(o));
    }
    
    private Object doTest(Object origin) {
        XMLSerializer serializer = new XMLSerializer();
        byte[] content = serializer.serialize(origin);
        Log.i(new String(content));
        return serializer.unserialize(content);
    }
    
    private void assertArray(Object[] a1, Object[] a2) {
        for (int i=0; i<a1.length; ++i) {
            assertEquals(a1[i],a2[i]);
        }
    }
}
