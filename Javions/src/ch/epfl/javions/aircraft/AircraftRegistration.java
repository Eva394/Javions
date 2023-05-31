package ch.epfl.javions.aircraft;


import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Represents the immatriculation of an aircraft
 *
 * @param string textual representation of the immatriculation
 * @author Eva Mangano 345375
 */
public record AircraftRegistration( String string ) {
	
	private static final Pattern IMMATRICULATION_PATTERN = Pattern.compile( "[A-Z0-9 .?/_+-]+" );
	
	
	/**
	 * Constructor. Builds an instance of AircraftRegistration
	 *
	 * @param string textual representation of the immatriculation
	 * @throws IllegalArgumentException if the string is not a valid immatriculation or is empty
	 * @author Eva Mangano 345375
	 */
	public AircraftRegistration {
		Preconditions.checkArgument( IMMATRICULATION_PATTERN.matcher( string )
		                                                    .matches() );
	}
}