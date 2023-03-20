package ch.epfl.javions.adsb;
/*
 *  Author :        Mangano Eva
 *  Date :          19/03/2023
 */

/**
 * Decoder of CPR positions
 */
/*public class CprDecoder {


    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent) {
        Preconditions.checkArgument( mostRecent == 0 || mostRecent == 1 );

        double longitude0;
        double longitude1;
        double latitude0;
        double latitude1;

        double nbZonesLat0 = 60;
        double nbZonesLat1 = 59;

        double widthLat0 = 1 / nbZonesLat0;
        double widthLat1 = 1 / nbZonesLat1;

        y0 = Math.scalb( y0, -17 );
        y1 = Math.scalb( y1, -17 );

        double temp = Math.rint( y0 * nbZonesLat1 - y1 * nbZonesLat0 );

        double zoneLat0 = temp;
        double zoneLat1 = temp;

        if ( temp < 0 ) {
            zoneLat0 += widthLat0;
            zoneLat1 += widthLat1;
        }

        latitude0 = widthLat0 * ( zoneLat0 + y0 );
        latitude1 = widthLat1 * ( zoneLat1 + y1 );

        if ( latitude > 90 || latitude < -90 ) {
            return null;
        }

        double longitudeT32 = Units.convert( longitude, Units.Angle.TURN, Units.Angle.T32 );
        double latitudeT32 = Units.convert( latitude, Units.Angle.TURN, Units.Angle.T32 );

        return new GeoPos( (int)longitude, (int)latitude );
    }
}
*/
