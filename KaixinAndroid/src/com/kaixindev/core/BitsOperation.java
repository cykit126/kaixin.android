package com.kaixindev.core;

/**
 * Helper class for bits operation.
 * @author Wilbur Luo
 */
public class BitsOperation {
	
	/**
	 * Check if a bit is on.
	 * @param v
	 * @param bits
	 * @return true for on, false for off.
	 */
    public static boolean isBitsOn(int v, int bits) {
        return (v & bits) != 0;
    }
    
	/**
	 * Check if a bit is on.
	 * @param v
	 * @param flag
	 * @return true for on, false for off.
	 */
    public static boolean isBitsOn(long v, long bits) {
        return (v & bits) != 0;
    }
    
    /**
     * Clear a bit of a integer.
     * @param v
     * @param flags
     * @return The integer with bits cleared.
     */
    public static int clearBits(int v, int bits) {
    	return v & ~bits;
    }
    
    /**
     * Clear a bit of a integer.
     * @param v
     * @param flags
     * @return The integer with bits cleared.
     */
    public static long clearBits(long v, long bits) {
    	return v & ~bits;
    }
}
