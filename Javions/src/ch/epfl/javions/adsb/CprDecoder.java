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


    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent) {
        Preconditions.checkArgument( mostRecent == 0 || mostRecent == 1 );

        double longitude0;
        double longitude1;
        double latitude0;
        double latitude1;

        // COMPUTATION OF THE LATITUDE
        //number of zones
        int nbZonesLat0 = 60;
        int nbZonesLat1 = 59;

        //normalization
        y0 = Math.scalb( y0, -17 );
        y1 = Math.scalb( y1, -17 );

        //zone where the aircraft is
        double temp = Math.rint( y0 * nbZonesLat1 - y1 * nbZonesLat0 );

        double zoneLat0 = temp;
        double zoneLat1 = temp;

        if ( temp < 0 ) {
            zoneLat0 += nbZonesLat0;
            zoneLat1 += nbZonesLat1;
        }

        //latitude
        double widthLat0 = 1. / nbZonesLat0;
        double widthLat1 = 1. / nbZonesLat1;

        latitude0 = widthLat0 * ( zoneLat0 + y0 );
        latitude1 = widthLat1 * ( zoneLat1 + y1 );

        //COMPUTATION OF THE LONGITUDE
        //number of zones for even and odd latitude, odd shoudl be one less than even
        int nbZonesLon0 = computeNbZonesLon( latitude0, widthLat0 );
        int nbZonesLon1 = computeNbZonesLon( latitude1, widthLat1 );
        if ( nbZonesLon0 - 1 != nbZonesLon1 ) {
            return null;
        }

        //

        //        if ( latitude0 > 90 || latitude0 < -90 ) {
        //            return null;
        //        }
        //
        //        double longitudeT32 = Units.convert( longitude, Units.Angle.TURN, Units.Angle.T32 );
        //        double latitudeT32 = Units.convert( latitude, Units.Angle.TURN, Units.Angle.T32 );
        //
        //        return new GeoPos( (int)longitude, (int)latitude );
        return null;
    }


    private static int computeNbZonesLon(double latitude, double widthLat) {

        latitude = Units.convertFrom( latitude, Units.Angle.TURN );

        double a = Math.acos(
                1 - ( ( 1 - Math.cos( 2 * Math.PI * widthLat ) ) / ( Math.cos( latitude ) * Math.cos( latitude ) ) ) );

        return Double.isNaN( a ) ? 1 : (int)Math.floor( 2 * Math.PI / a );
    }
}
