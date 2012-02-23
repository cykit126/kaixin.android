package com.kaixindev.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This is the KeyValueBackend implementation, provides operations between maps,
 * such as merging, diff-calculating and so on.
 * @author Wilbur Luo
 */
public class Differ {
	public static final int OP_PUT = 1;
	public static final int OP_REMOVE = 2;
	
	/**
	 * Generate diff patch for the left Map against the right Map.
	 * The patch can be applied on the left Map.
	 * @param left Patch generated for this Map.
	 * @param right Map to be compared against.
	 * @return That patch that can be applied on the left Map.
	 */
	public static List<PatchItem> diff(Map<String,Value> left, Map<String,Value> right) {
		List<PatchItem> patch = new ArrayList<PatchItem>();
		
		if (left == null || right == null) {
		    return patch;
		}
		
		for (String key : right.keySet()) {
			if (left.containsKey(key)) {
				Value leftValue = left.get(key);
				Value rightValue = right.get(key);
				if (!leftValue.equals(rightValue)) {
					patch.add(new PatchItem(OP_PUT,key,right.get(key)));
				}
			} else {
				patch.add(new PatchItem(OP_PUT,key,right.get(key)));
			}
		}
		
		for (String key : left.keySet()) {
			if (!right.containsKey(key)) {
				patch.add(new PatchItem(OP_REMOVE,key));
			}
		}
		
		return patch;
	}
	
	/**
	 * Apply patch on the data.
	 * @param data Data to be applied.
	 * @param patch Patch to use.
	 */
	public static void apply(Map<String,Value> data, List<PatchItem> patch) {
		for (PatchItem item : patch) {
			switch (item.getOperation()) {
			case OP_PUT:
				data.put(item.getKey(), item.getValue());
				break;
			case OP_REMOVE:
				data.remove(item.getKey());
				break;
			}
		}
	}
}
