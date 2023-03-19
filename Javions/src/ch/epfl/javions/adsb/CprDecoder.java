package ch.epfl.javions.adsb;
/*
 *  Author :        Mangano Eva
 *  Date :          19/03/2023
 */


import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;

/**
 * Decoder of CPR positions
 */
public class CprDecoder {


    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent) {
        Preconditions.checkArgument( mostRecent == 0 || mostRecent == 1 );

        int longitude;
        int latitude;

        return new GeoPos()
    }
}
