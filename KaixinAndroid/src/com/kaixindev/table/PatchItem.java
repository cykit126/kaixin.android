package com.kaixindev.table;

/**
 * Operation that can be applied on Map, such as deleting value, 
 * putting value.
 * @author Wilbur Luo
 */
public class PatchItem {
	private Value value = null;
	private String key = null;
	private int op = 0;
	
	public PatchItem(int op) {
		this.op = op;
	}
	
	public PatchItem(int op, String key) {
		this.op = op;
		this.key = key;
	}
	
	public PatchItem(int op, String key, Value value) {
		this.op = op;
		this.key = key;
		this.value = value;
	}

	public Value getValue() {
		return value;
	}

	public String getKey() {
		return key;
	}

	public int getOperation() {
		return op;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{op=")
			.append(getOpName())
			.append(", key=")
			.append(key)
			.append(", value=")
			.append(value==null ? "null" : value.toString())
			.append("}");
		return sb.toString();
	}
	
	private String getOpName() {
		switch (op) {
		case Differ.OP_PUT:
			return "OP_PUT";
		case Differ.OP_REMOVE:
			return "OP_REMOVE";
		default:
			return "OP_UNKNOWN";
		}
	}
}
