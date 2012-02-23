package com.gaya3d.table.test;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.gaya3d.android.FileSystem;
import com.gaya3d.android.Log;
import com.gaya3d.table.Differ;
import com.gaya3d.table.FileStorage;
import com.gaya3d.table.JSONSerializer;
import com.gaya3d.table.PatchItem;
import com.gaya3d.table.Storage;
import com.gaya3d.table.Table;
import com.gaya3d.table.Value;
import com.gaya3d.table.XMLSerializer;

import android.test.AndroidTestCase;

public class TableTest extends AndroidTestCase {
	
	public void testGetterSetter() {
		Table table = new Table();
		table.set("string", "string");
		table.set("int", 9);
		table.set("float", 1.1f);
		
		assertEquals("string",table.getString("string",null));
		assertEquals(9,table.getInt("int", 0));
		assertEquals(1.1f,table.getFloat("float",0.0f));
	}
	
	public void testStorageWithXMLSerializer() {
		Table table = new Table();
		
		// Add internal JSON file storage
		Log.i("Initialize internal storage","TableTest");
		File internal = FileSystem.getInternalCacheFile(
				getContext(), "TableTest", "testStorage");
		assertNotNull(internal);
		Storage internalStorage = FileStorage.newInstance(new XMLSerializer(),internal);
		table.addStorage(internalStorage);
		
		// Add external JSON file storage
		Log.i("Intialize external storage","TableTest");
		File external = FileSystem.getExternalCacheFile(
				getContext(), "TableTest", "testStorage");
		assertNotNull(external);
		Storage externalStorage = FileStorage.newInstance(new XMLSerializer(),external);
		table.addStorage(externalStorage);
		
		// Set data and synchronize.
		Log.i("Set data","TableTest");
		table.set("string", "string");
		table.set("int", 9);
		table.set("float", 1.1f);
		
		Log.i("Start to synchronize","TableTest");
		table.synchronize();
		assertTrue(table.flush());
		Log.i("Data synchronized.","TableTest");
		
		// Check internal data
		Map<String,Value> internalData = internalStorage.getData();
		assertNotNull(internalData);
		Log.d(internalData.toString(),"internal data");
		
		assertEquals(3,internalData.size());
		
		assertNotNull(internalData.get("string"));
		assertEquals("string",internalData.get("string").data);
		
		assertNotNull(internalData.get("int"));
		assertEquals(9,internalData.get("int").data);
		
		assertNotNull(internalData.get("float"));
		assertEquals(1.1f,internalData.get("float").data);
		
		// Check external data
		Map<String,Value> externalData = externalStorage.getData();
		assertNotNull(externalData);
		Log.d(externalData.toString(),"external data");
		
		List<PatchItem> patch = Differ.diff(externalData, internalData);
		Log.d(patch.toString(),"patch");
		assertTrue(patch.isEmpty());
	}
	
    public void testStorageWithJSONSerializer() {
        Table table = new Table();
        
        // Add internal JSON file storage
        Log.i("Initialize internal storage","TableTest");
        File internal = FileSystem.getInternalCacheFile(
                getContext(), "TableTest", "testStorage");
        assertNotNull(internal);
        Storage internalStorage = FileStorage.newInstance(new JSONSerializer(),internal);
        table.addStorage(internalStorage);
        
        // Add external JSON file storage
        Log.i("Intialize external storage","TableTest");
        File external = FileSystem.getExternalCacheFile(
                getContext(), "TableTest", "testStorage");
        assertNotNull(external);
        Storage externalStorage = FileStorage.newInstance(new JSONSerializer(),external);
        table.addStorage(externalStorage);
        
        // Set data and synchronize.
        Log.i("Set data","TableTest");
        table.set("string", "string");
        table.set("int", 9);
        table.set("float", 1.1f);
        
        Log.i("Start to synchronize","TableTest");
        table.synchronize();
        assertTrue(table.flush());
        Log.i("Data synchronized.","TableTest");
        
        // Check internal data
        Map<String,Value> internalData = internalStorage.getData();
        assertNotNull(internalData);
        Log.d(internalData.toString(),"internal data");
        
        assertEquals(3,internalData.size());
        
        assertNotNull(internalData.get("string"));
        assertEquals("string",internalData.get("string").data);
        
        assertNotNull(internalData.get("int"));
        assertEquals(9,internalData.get("int").data);
        
        assertNotNull(internalData.get("float"));
        assertEquals(1.1f,internalData.get("float").data);
        
        // Check external data
        Map<String,Value> externalData = externalStorage.getData();
        assertNotNull(externalData);
        Log.d(externalData.toString(),"external data");
        
        List<PatchItem> patch = Differ.diff(externalData, internalData);
        Log.d(patch.toString(),"patch");
        assertTrue(patch.isEmpty());
    }	
}
