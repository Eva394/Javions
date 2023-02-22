package ch.epfl.javions;
/*
 *  Author :        Mangano Eva
 *  Date :          21/02/2023
 */


import java.util.Objects;

public class Bits {
	
	private Bits() {
	
	}
	
	
	@Override
	public String toString() {
		
		return super.toString();
	}
	
	
	public int extractUInt(long value, int start, int size) {
		
		Preconditions.checkArgument(! (size > 0 && size < Integer.SIZE));
		
		Objects.checkFromIndexSize(start, size, Long.SIZE) ;
		
		value
	}
	
	public boolean testBit(long value, int index) {
		
		Objects.checkIndex(index, Long.SIZE) ;
		
		long mask = 1L << index ;
		
		if ( (value & mask) == mask ) {
			return true ;
		}
		
		return false ;
	}
	
}
