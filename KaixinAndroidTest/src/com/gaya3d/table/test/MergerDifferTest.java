package com.gaya3d.table.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gaya3d.android.Log;
import com.gaya3d.table.Differ;
import com.gaya3d.table.PatchItem;
import com.gaya3d.table.Merger;
import com.gaya3d.table.Value;

import android.test.AndroidTestCase;

public class MergerDifferTest extends AndroidTestCase {
	
	public void testMerge() {
		Map<String,Value> map1 = new HashMap<String,Value>();
		map1.put("test1", new Value("value1",1));
		map1.put("test2", new Value("value2",2));
		map1.put("test3", new Value("value3",3));
		
		Map<String,Value> map2 = new HashMap<String,Value>();
		map2.put("test1", new Value("value11",2));
		map2.put("test8", new Value("value8",8));
		map2.put("test9", new Value("value9",9));
		
		Merger.merge(map1, map2);
		
		assertEquals(5,map1.size());
		assertTrue(map1.containsKey("test1"));
		assertEquals("value11",map1.get("test1").data);
		assertEquals(2,map1.get("test1").version);
		
		assertTrue(map1.containsKey("test2"));
		assertEquals("value2",map1.get("test2").data);
		assertEquals(2,map1.get("test1").version);
		
		assertTrue(map1.containsKey("test3"));
		assertEquals("value3",map1.get("test3").data);
		assertEquals(3,map1.get("test3").version);
		
		assertTrue(map1.containsKey("test8"));
		assertEquals("value8",map1.get("test8").data);
		assertEquals(8,map1.get("test8").version);
		
		assertTrue(map1.containsKey("test9"));
		assertEquals("value9",map1.get("test9").data);
		assertEquals(9,map1.get("test9").version);
	}
	
	public void testDiffEqual() {
		Map<String,Value> map1 = new HashMap<String,Value>();
		map1.put("test1", new Value("value1",1));
		map1.put("test2", new Value("value2",3));
		map1.put("test3", new Value("value3",2));
		
		Map<String,Value> map2 = new HashMap<String,Value>();
		map2.put("test2", new Value("value2",3));
		map2.put("test3", new Value("value3",2));
		map2.put("test1", new Value("value1",1));
		
		List<PatchItem> patch = Differ.diff(map1, map2);
		assertTrue(patch.isEmpty());
	}
	
	public void testDiffPut() {
		Map<String,Value> map1 = new HashMap<String,Value>();
		map1.put("test1", new Value("value1",1));
		map1.put("test2", new Value("value2",3));
		map1.put("test3", new Value("value3",2));
		
		Map<String,Value> map2 = new HashMap<String,Value>();
		
		List<PatchItem> patch = Differ.diff(map2, map1);
		Log.d(patch.toString(),"patch");
		assertEquals(3,patch.size());
		assertEquals(Differ.OP_PUT,patch.get(0).getOperation());
		assertEquals(Differ.OP_PUT,patch.get(1).getOperation());
		assertEquals(Differ.OP_PUT,patch.get(2).getOperation());
	}
	
	public void testDiffRemove() {
		Map<String,Value> map1 = new HashMap<String,Value>();
		map1.put("test1", new Value("value1",1));
		map1.put("test2", new Value("value2",3));
		map1.put("test3", new Value("value3",2));
		
		Map<String,Value> map2 = new HashMap<String,Value>();	
		
		List<PatchItem> patch = Differ.diff(map1, map2);
		Log.d(patch.toString(),"patch");
		assertEquals(3,patch.size());
		assertEquals(Differ.OP_REMOVE,patch.get(0).getOperation());
		assertEquals(Differ.OP_REMOVE,patch.get(1).getOperation());
		assertEquals(Differ.OP_REMOVE,patch.get(2).getOperation());
	}
}
