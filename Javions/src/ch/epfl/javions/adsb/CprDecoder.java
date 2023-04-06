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
 * @author Nagyung Kim (339628)
 */
public class CprDecoder {


    private static final int EVEN_MESSAGE = 0;
    private static final int ODD_MESSAGE = 1;
    private static final double NB_ZONES_LAT_0 = 60.;
    private static final double NB_ZONES_LAT_1 = 59.;


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
        double[] returns = compute_LatOrLon_Zone( y0, y1, NB_ZONES_LAT_0, NB_ZONES_LAT_1 );
        y0 = returns[0];
        y1 = returns[1];
        double widthLat0 = returns[2];
        double widthLat1 = returns[3];

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

        double[] xs = compute_LatOrLon_Zone( x0, x1, nbZonesLon0, nbZonesLon1 );
        x0 = xs[0];
        x1 = xs[1];

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


    private static double[] compute_LatOrLon_Zone(double p0, double p1, double zone0, double zone1) {
        double temp = getProductDifference( p0, p1, zone0, zone1 );
        double zoneLat0 = computeZone( temp, zone0 );
        double zoneLat1 = computeZone( temp, zone1 );
        double width0 = computeWidth( zone0 );
        double width1 = computeWidth( zone1 );
        p0 = computeLatOrLon( p0, zoneLat0, width0 );
        p1 = computeLatOrLon( p1, zoneLat1, width1 );
        double[] doubles = {p0, p1, width0, width1};
        return doubles;
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
