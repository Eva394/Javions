package ch.epfl.javions.aircraft;


import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Represents the type designator of an aircraft
 *
 * @param string textual representation of the type designator
 * @author Eva Mangano 345375
 */
public record AircraftTypeDesignator( String string ) {
    
    private static final Pattern TYPE_DESIGNATOR_PATTERN = Pattern.compile( "[A-Z0-9]{2,4}" );
    
    
    /**
     * Constructor. Builds an instance of AircraftTypeDesignator
     *
     * @param string textual representation of the type designator
     * @throws IllegalArgumentException if the string is not a valid type designator
     * @author Eva Mangano 345375
     */
    public AircraftTypeDesignator {
        Preconditions.checkArgument( TYPE_DESIGNATOR_PATTERN.matcher( string )
                                                            .matches() || string.isEmpty() );
    }
}