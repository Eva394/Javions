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
//		if ( ! (size > 0 && size < 32) ) {                  //replce with use of
//			// checkFromIndexSize and checkIndex, using attributes Integer.SIZE and Long.SIZE
//		throw new IllegalArgumentException() ;
//		}
		try {
			if ( ! (Objects.checkIndex(size, Integer.SIZE) && (size > 0)) ) {
				throw new IllegalArgumentException() ;
			}
		}
		catch ( IndexOutOfBoundsException ignored ) {
		
		}
		
		
		if ( ! Objects.checkFromIndexSize(start, size, Long.SIZE) ) {
			throw new
		}
		
		if ( ! ((size-start) >= 0 && (size-start) < 64) ) {
			throw new IndexOutOfBoundsException() ;
		}
		
		
		return ( );        //use shifting bits operators << >> >>> seen in class
		//https://stackoverflow.com/questions/8011700/how-do-i-extract-specific-n-bits-of-a-32-bit
		// -unsigned-integer-in-c
	}
	
	Objects.checkIndex()
	public boolean testBit(long value, int index) {
		if (  )
	}
	
}
