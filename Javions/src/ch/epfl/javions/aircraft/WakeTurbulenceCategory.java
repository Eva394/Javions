package ch.epfl.javions.aircraft;

public enum WakeTurbulenceCategory {

    LIGHT,
    MEDIUM,
    HEAVY,
    UNKNOWN;


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