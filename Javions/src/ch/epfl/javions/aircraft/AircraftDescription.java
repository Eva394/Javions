package ch.epfl.javions.aircraft;
/*
 *  Author :        Mangano Eva
 *  Date :          26/02/2023
 */


import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Represents the description of an aircraft
 *
 * @param string textual representation of the description
 * @author Eva Mangano 345375
 */
public record AircraftDescription(String string) {

    /**
     * Description
     */
    //TODO find out if this is public or private
    private static Pattern descriptionPattern;


    /**
     * Constructor. Builds an instance of AircraftDescription
     *
     * @param string textual representation of the description
     * @throws IllegalArgumentException if the string is not a valid description
     */
    public AircraftDescription {
        descriptionPattern = Pattern.compile( "[ABDGHLPRSTV-][0123468][EJPT-]" );
        Preconditions.checkArgument( descriptionPattern.matcher( string ).matches() || string.equals( "" ) );
    }
}