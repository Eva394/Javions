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
 * @author Eva Mangano 345375
 */
public class CprDecoder {


    public static final int EVEN_MESSAGE = 0;
    public static final int ODD_MESSAGE = 1;
    public static final double NB_ZONES_LAT_0 = 60.;
    public static final double NB_ZONES_LAT_1 = 59.;


    /**
     * @param x0         the even longitude
     * @param y0         the even latitude
     * @param x1         the odd longitude
     * @param y1         the odd latitude
     * @param mostRecent parity of the most recent position message, 0 for even or 1 for odd
     * @return the geographical position corresponding to the local positions <code>x0</code>, <code>y0</code>,
     * <code>x1</code> and <code>y1</code> (depending the on the most recent message), or null if the positon is
     * invalid or can't be determined
     * @throws IllegalArgumentException if <code>mostRecent</code> is invalid (not 0 or 1)
     * @author Eva Mangano 345375
     */
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent) {
        Preconditions.checkArgument( mostRecent == EVEN_MESSAGE || mostRecent == ODD_MESSAGE );
        // COMPUTATION OF THE LATITUDE
        ///zone where the aircraft is
        double temp = getProductDifference( y0, y1, NB_ZONES_LAT_0, NB_ZONES_LAT_1 );
        double zoneLat0 = computeZone( temp, NB_ZONES_LAT_0 );
        double zoneLat1 = computeZone( temp, NB_ZONES_LAT_1 );

        //TODO FIND HOW TO MODULATE THIS
        ///latitude
        double widthLat0 = computeWidth( NB_ZONES_LAT_0 );
        double widthLat1 = computeWidth( NB_ZONES_LAT_1 );
        y0 = computeLatOrLon( y0, zoneLat0, widthLat0 );
        y1 = computeLatOrLon( y1, zoneLat1, widthLat1 );

        //COMPUTATION OF THE LONGITUDE
        ///number of zones for even and odd latitude, odd should be one less than even
        double nbZonesLon0 = computeNbZonesLon( y0, widthLat0 );
        double nbZonesLon1 = computeNbZonesLon( y1, widthLat0 );
        if ( nbZonesLon0 != nbZonesLon1 ) {
            return null;
        }
        if ( nbZonesLon0 != 1 ) {
            nbZonesLon1 = nbZonesLon0 - 1;
        }

        ///zone where the aircraft is
        temp = getProductDifference( x0, x1, nbZonesLon0, nbZonesLon1 );
        double zoneLon0 = computeZone( temp, nbZonesLon0 );
        double zoneLon1 = computeZone( temp, nbZonesLon1 );

        ///longitude
        double widthLon0 = computeWidth( nbZonesLon0 );
        double widthLon1 = computeWidth( nbZonesLon1 );
        x0 = computeLatOrLon( x0, zoneLon0, widthLon0 );
        x1 = computeLatOrLon( x1, zoneLon1, widthLon1 );

        //RETURN THE RIGHT ONE
        double longitude;
        double latitude;

        switch ( mostRecent ) {
            case EVEN_MESSAGE -> {
                longitude = convert( x0 );
                latitude = convert( y0 );
            }
            case ODD_MESSAGE -> {
                longitude = convert( x1 );
                latitude = convert( y1 );
            }
            default -> {
                throw new Error();
            }
        }

        return isValidLatitude( latitude ) ? new GeoPos( (int)Math.rint( longitude ), (int)Math.rint( latitude ) )
                                           : null;
    }


    private static double convert(double x0) {
        return Units.convert( recenter( x0 ), Units.Angle.TURN, Units.Angle.T32 );
    }


    private static double computeWidth(double nbZones) {
        return 1. / nbZones;
    }


    private static double computeLatOrLon(double value, double zone, double zoneWidth) {
        return zoneWidth * ( zone + value );
    }


    private static double computeZone(double temp, double nbZones) {
        return temp < 0 ? temp + nbZones : temp;
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
