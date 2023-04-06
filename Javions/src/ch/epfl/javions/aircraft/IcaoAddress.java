package ch.epfl.javions.aircraft;
/*
 *  Author :        Mangano Eva
 *  Date :          26/02/2023
 */


import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Represents an ICAO address
 * @param string textual representation of the ICAO address
 * @author Eva Mangano 345375
 */
public record IcaoAddress(String string) {

    /**
     * ICAO address
     */
    private static Pattern addressPattern;


    /**
     * Constructor. Builds an instance of IcaoAcdress
     * @param string textual representation of the ICAO address
     * @throws IllegalArgumentException if the string is not a valid ICAO address or is empty
     * @author Eva Mangano 345375
     */
    public IcaoAddress {
        addressPattern = Pattern.compile( "[0-9A-F]{6}" );
        Preconditions.checkArgument( addressPattern.matcher( string )
                                                   .matches() );
    }
}
