package ch.epfl.javions.aircraft;

/**
 * Wake turbulence Category
 *
 * @author Nagyung Kim (339628)
 */

public enum WakeTurbulenceCategory {

    LIGHT,
    MEDIUM,
    HEAVY,
    UNKNOWN;

    /**
     * returns textual values in the database
     * converted into items of the enumerated type
     *
     * @param s
     *         textual values in the database (L, M, H, UNKNOWN)
     * @return textual values in the database
     *          converted into items of the enumerated type
     */

    public static WakeTurbulenceCategory of(String s) {
        if ( s.equals( "L" ) ) {
            return LIGHT;
        }
        else if ( s.equals( "M" ) ) {
            return MEDIUM;
        }
        else if ( s.equals( "H" ) ) {
            return HEAVY;
        }
        else {
            return UNKNOWN;
        }
    }
}