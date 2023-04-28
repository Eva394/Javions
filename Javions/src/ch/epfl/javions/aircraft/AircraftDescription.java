package ch.epfl.javions.aircraft;


import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Represents the description of an aircraft
 * @param string textual representation of the description
 * @author Eva Mangano 345375
 */
public record AircraftDescription(String string) {

    public static final Pattern DESCRIPTION_PATTERN = Pattern.compile( "[ABDGHLPRSTV-][0123468][EJPT-]" );


    /**
     * Constructor. Builds an instance of AircraftDescription
     * @param string textual representation of the description
     * @throws IllegalArgumentException if the string is not a valid description
     * @author Eva Mangano 345375
     */
    public AircraftDescription {
        Preconditions.checkArgument( DESCRIPTION_PATTERN.matcher( string )
                                                        .matches() || string.isEmpty() );
    }
}