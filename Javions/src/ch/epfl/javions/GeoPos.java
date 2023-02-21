package ch.epfl.javions;
/*
 *  Author :        Mangano Eva
 *  Date :          21/02/2023
 */




public record GeoPos(int longitudet32, int latitudet32) {
	
	public GeoPos {
		if ( !isValidLatitudeT32(latitudet32) ) {
			throw new IllegalArgumentException() ;
		}
	}

	static boolean isValidLatitudeT32(int latitudeT32) {
		return ( -Math.scalb(1,30) <= latitudeT32 && latitudeT32 <= Math.scalb(1,30) ) ;
	}
	
	double longitude() {
		return Units.convertFrom(longitudet32, Units.Angle.T32) ;
	}
	
	double latitude() {
		return Units.convertFrom(latitudet32, Units.Angle.T32) ;
	}
	
	
	@Override
	public String toString() {
		
		return  ;
	}
}
