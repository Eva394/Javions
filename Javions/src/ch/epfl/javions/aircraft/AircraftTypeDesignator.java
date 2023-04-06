package ch.epfl.javions.aircraft;
/*
 *  Author :        Mangano Eva
 *  Date :          26/02/2023
 */


import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Represents the type designator of an aircraft
 * @param string textual representation of the type designator
 * @author Eva Mangano 345375
 */
public record AircraftTypeDesignator(String string) {

    /**
     * Type designator
     */
    private static Pattern typeDesignatorPattern;


    /**
     * Constructor. Builds an instance of AircraftTypeDesignator
     * @param string textual representation of the type designator
     * @throws IllegalArgumentException if the string is not a valid type designator
     * @author Eva Mangano 345375
     */
    public AircraftTypeDesignator {
        typeDesignatorPattern = Pattern.compile( "[A-Z0-9]{2,4}" );
        Preconditions.checkArgument( typeDesignatorPattern.matcher( string )
                                                          .matches() || string.equals( "" ) );
    }
}