package ch.epfl.javions;
/*
 *  Author :        Mangano Eva
 *  Date :          21/02/2023
 */




public record GeoPos(int longitudeT32, int latitudeT32) {
	
	public GeoPos {
		Preconditions.checkArgument(isValidLatitudeT32(latitudeT32)) ;
	}

	public static boolean isValidLatitudeT32(int latitudeT32) {
		return (Math.scalb(-1., 30) <= latitudeT32 && latitudeT32 <= Math.scalb(1., 30)) ;
	}
	
	public double longitude() {
		return Units.convertFrom(longitudeT32, Units.Angle.T32) ;
	}
	
	public double latitude() {
		return Units.convertFrom(latitudeT32, Units.Angle.T32) ;
	}
	
	
	@Override
	public String toString() {
		
		return  ("(" + Units.convert(longitudeT32, Units.Angle.T32, Units.Angle.DEGREE) + "°, " + Units.convert(latitudeT32, Units.Angle.T32, Units.Angle.DEGREE) + "°)") ;
	}
}
