package ch.epfl.javions.adsb;
/*
 *  Author :        Mangano Eva
 *  Date :          19/03/2023
 */

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

/**
 * Decoder of CPR positions
 */
public class CprDecoder {


    /**
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @param mostRecent
     * @return
     */
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent) {
        Preconditions.checkArgument( mostRecent == 0 || mostRecent == 1 );

        // COMPUTATION OF THE LATITUDE
        //number of zones
        double nbZonesLat0 = 60.;
        double nbZonesLat1 = 59.;

        //zone where the aircraft is
        double temp = getProductDifference( y0, y1, nbZonesLat0, nbZonesLat1 );

        double zoneLat0 = temp;
        double zoneLat1 = temp;
        if ( temp < 0 ) {
            zoneLat0 += nbZonesLat0;
            zoneLat1 += nbZonesLat1;
        }

        //latitude
        double widthLat0 = 1. / nbZonesLat0;
        double widthLat1 = 1. / nbZonesLat1;

        y0 = widthLat0 * ( zoneLat0 + y0 );
        y1 = widthLat1 * ( zoneLat1 + y1 );

        //COMPUTATION OF THE LONGITUDE
        //number of zones for even and odd latitude, odd should be one less than even
        double nbZonesLon0 = computeNbZonesLon( y0, widthLat0 );
        double nbZonesLon1 = computeNbZonesLon( y1, widthLat0 );
        if ( nbZonesLon0 != nbZonesLon1 ) {
            return null;
        }
        if ( nbZonesLon0 != 1 ) {
            nbZonesLon1 = nbZonesLon0 - 1;
        }

        //zone where the aircraft is
        temp = getProductDifference( x0, x1, nbZonesLon0, nbZonesLon1 );

        double zoneLon0 = temp;
        double zoneLon1 = temp;

        if ( temp < 0 ) {
            zoneLon0 += nbZonesLon0;
            zoneLon1 += nbZonesLon1;
        }

        //longitude
        double widthLon0 = 1. / nbZonesLon0;
        double widthLon1 = 1. / nbZonesLon1;

        x0 = widthLon0 * ( zoneLon0 + x0 );
        x1 = widthLon1 * ( zoneLon1 + x1 );

        //RETURN THE RIGHT ONE
        double longitude;
        double latitude;

        if ( mostRecent == 0 ) {
            longitude = Units.convert( recenter( x0 ), Units.Angle.TURN, Units.Angle.T32 );
            latitude = Units.convert( recenter( y0 ), Units.Angle.TURN, Units.Angle.T32 );
        }
        else {
            longitude = Units.convert( recenter( x1 ), Units.Angle.TURN, Units.Angle.T32 );
            latitude = Units.convert( recenter( y1 ), Units.Angle.TURN, Units.Angle.T32 );
        }

        return isValidLatitude( latitude ) ? new GeoPos( (int)Math.rint( longitude ), (int)Math.rint( latitude ) )
                                           : null;
    }


    private static boolean isValidLatitude(double y) {
        double yDegrees = Units.convert( y, Units.Angle.T32, Units.Angle.DEGREE );
        return -90. <= yDegrees && yDegrees <= 90.;
    }


    private static double recenter(double value) {
        return ( value > .5 ) ? value - 1 : value;
    }


    private static double getProductDifference(double y0, double y1, double nbZonesLat0, double nbZonesLat1) {
        return Math.rint( y0 * nbZonesLat1 - y1 * nbZonesLat0 );
    }


    private static int computeNbZonesLon(double latitude, double widthLat) {

        latitude = Units.convertFrom( latitude, Units.Angle.TURN );

        double a = Math.acos(
                1 - ( ( 1 - Math.cos( 2 * Math.PI * widthLat ) ) / ( Math.cos( latitude ) * Math.cos( latitude ) ) ) );

        return Double.isNaN( a ) ? 1 : (int)Math.floor( 2 * Math.PI / a );
    }
}
