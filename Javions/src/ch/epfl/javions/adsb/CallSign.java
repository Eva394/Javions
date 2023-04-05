package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;


/**
 * call sign of an aircraft
 * @author Nagyung Kim (339628)
 */

public record CallSign(String string) {

    private static Pattern CallSign;


    /**
     * Builds an instance of call sign
     * @param string string of the callsign
     * @throws IllegalArgumentException if the string is not the valid type of call sign
     */
    public CallSign {
        CallSign = Pattern.compile( "[A-Z0-9 ]{0,8}" );
        Preconditions.checkArgument( CallSign.matcher( string )
                                             .matches() || string.equals( "" ) );
    }
}