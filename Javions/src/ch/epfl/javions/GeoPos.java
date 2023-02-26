package ch.epfl.javions;
/*
 *  Author :        Mangano Eva
 *  Date :          21/02/2023
 */




/**
 * @author Eva Mangano 345375
 * Geographic coodinates (couple longitude and latitude), expressed in T32 and stocked as ints
 * @param longitudeT32 longitude, expressed in T32
 * @param latitudeT32 latitude, expressed in T32
 */
public record GeoPos(int longitudeT32, int latitudeT32) {
	
	/**
     * Constructor. Builds an instance of GeoPos.
     *
     * @param longitudeT32 longitude, expressed in T32
     * @param latitudeT32  latitude, expressed in T32
     * @throws IllegalArgumentException if the latitude is not between -2^30 and 2^30
     * @author Eva Mangano 345375
     */
	public GeoPos {
		Preconditions.checkArgument(isValidLatitudeT32(latitudeT32)) ;
	}
	
	
	/**
	 * Checks if the latitude is valid
	 * @author Eva Mangano 345375
	 * @param latitudeT32 latitude, expressed in T32
	 * @return a boolean value, true if the latitude is valid
	 */
	public static boolean isValidLatitudeT32(int latitudeT32) {
		return (Math.scalb(-1., 30) <= latitudeT32 && latitudeT32 <= Math.scalb(1., 30)) ;
	}
	
	
	/**
	 * Converts the longitude of the instance to radians
	 * @author Eva Mangano 345375
	 * @return the longitude of the instance converted to radians
	 */
	public double longitude() {
		return Units.convertFrom(longitudeT32, Units.Angle.T32) ;
	}
	
	
	/**
	 * Converts the latitude of the instance to radians
	 * @author Eva Mangano 345375
	 * @return the latitude of the instance converted to radians
	 */
	public double latitude() {
		return Units.convertFrom(latitudeT32, Units.Angle.T32) ;
	}
	
	
	/**
	 * Returns a string of the form (longitude, latitude) once converted to degrees
	 * @author Eva Mangano 345375
	 * @return a string of the form (laongitude, latitude), expressed in degrees
	 */
	@Override
	public String toString() {
		
		return  ("(" + Units.convert(longitudeT32, Units.Angle.T32, Units.Angle.DEGREE) + "°, " + Units.convert(latitudeT32, Units.Angle.T32, Units.Angle.DEGREE) + "°)") ;
	}
}
