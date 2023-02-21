package ch.epfl.javions;
/*
 *  Author :        Mangano Eva
 *  Date :          21/02/2023
 */




public class Bits {
	
	private Bits() {
	
	}
	
	
	@Override
	public String toString() {
		
		return super.toString();
	}
	
	
	public int extractUInt(long value, int start, int size) {
		if ( ! (size > 0 && size < 32) ) {
		throw new IllegalArgumentException() ;
		}
		
		if ( ! ((size-start) >= 0 && (size-start) < 64) ) {
			throw new IndexOutOfBoundsException() ;
		}
		
		
		//return ();        //use shifting bits operators << >> >>> seen in class
	}
	
}
