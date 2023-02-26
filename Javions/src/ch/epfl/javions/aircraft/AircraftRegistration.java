package ch.epfl.javions.aircraft;
/*
 *  Author :        Mangano Eva
 *  Date :          26/02/2023
 */


import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Represents the immatriculation of an aircraft
 *
 * @param string textual representation of the immatriculation
 * @author Eva Mangano 345375
 */
public record AircraftRegistration(String string) {

    /**
     * Immatriculation
     */
    //TODO find out if this is public or private
    private static Pattern immatriculationPattern;


    /**
     * Constructor. Builds an instance of AircraftRegistration
     *
     * @param string textual representation of the immatriculation
     * @throws IllegalArgumentException if the string is not a valid immatriculation or is empty
     */
    public AircraftRegistration {
        immatriculationPattern = Pattern.compile( "[A-Z0-9 .?/_+-]+" );
        Preconditions.checkArgument( immatriculationPattern.matcher( string ).matches() );
    }
}