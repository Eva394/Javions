package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;


/**
 * call sign of an aircraft
 * @author Nagyung Kim (339628)
 */

public record CallSign(String string) {

    public static final Pattern CALL_SIGN = Pattern.compile( "[A-Z0-9 ]{0,8}" );


    /**
     * Builds an instance of call sign
     * @param string string of the callsign
     * @throws IllegalArgumentException if the string is not the valid type of call sign
     */
    public CallSign {
        Preconditions.checkArgument( CALL_SIGN.matcher( string )
                                              .matches() || string.isEmpty() );
    }
}