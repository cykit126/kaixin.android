package com.gaya3d.table.test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gaya3d.android.FileSystem;
import com.gaya3d.android.Log;
import com.gaya3d.table.Differ;
import com.gaya3d.table.FileStorage;
import com.gaya3d.table.JSONSerializer;
import com.gaya3d.table.PatchItem;
import com.gaya3d.table.Value;

import android.test.AndroidTestCase;

public class FileStorageTest extends AndroidTestCase {
	
	private File mTestFile = null;
	
	protected void setUp() {
	    Log.ENABLED = true;
		mTestFile = FileSystem.getExternalCacheFile(getContext(), "test", "test.json");
		assertNotNull(mTestFile);
	}
	
	public void testNewInstance() {
		FileStorage storage = FileStorage.newInstance(new JSONSerializer(),mTestFile);
		assertNotNull(storage);
	}
	
	public void testJSON() {
		Map<String,Value> data = new HashMap<String,Value>();
		data.put("test1", new Value("value1",1));
		data.put("test2", new Value("value2",1));
		data.put("test3", new Value("value3",1));
		Log.d(data.toString(),"data");
		
		FileStorage storage = FileStorage.newInstance(new JSONSerializer(),mTestFile);
		assertNotNull(storage);
		assertTrue(storage.update(data));
		
		Map<String,Value> data2 = storage.getData();
		List<PatchItem> patch = Differ.diff(data,data2);
		Log.d(data2.toString(),"data2");
		Log.d(patch.toString(),"patch");
		assertTrue(patch.isEmpty());
	}
	
    public void testXML() {
        Map<String,Value> data = new HashMap<String,Value>();
        data.put("test1", new Value("value1",1));
        data.put("test2", new Value("value2",1));
        data.put("test3", new Value("value3",1));
        Log.d(data.toString(),"data");
        
        /*
        FileStorage storage = FileStorage.newInstance(new XMLSerializer(),mTestFile);
        assertNotNull(storage);
        assertTrue(storage.update(data));
        
        Map<String,Value> data2 = storage.getData();
        assertNotNull(data2);
        List<PatchItem> patch = Differ.diff(data,data2);
        Log.d(data2.toString(),"data2");
        Log.d(patch.toString(),"patch");
        assertTrue(patch.isEmpty());
        */
    }	
}
