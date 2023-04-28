package ch.epfl.javions.aircraft;


import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Represents an ICAO address
 * @param string textual representation of the ICAO address
 * @author Eva Mangano 345375
 */
public record IcaoAddress(String string) {

    private static final Pattern ICAO_ADDRESS_PATTERN = Pattern.compile( "[0-9A-F]{6}" );


    /**
     * Constructor. Builds an instance of IcaoAcdress
     * @param string textual representation of the ICAO address
     * @throws IllegalArgumentException if the string is not a valid ICAO address or is empty
     * @author Eva Mangano 345375
     */
    public IcaoAddress {

        Preconditions.checkArgument( ICAO_ADDRESS_PATTERN.matcher( string )
                                                         .matches() );
    }
}
