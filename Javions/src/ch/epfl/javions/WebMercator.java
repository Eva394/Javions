package ch.epfl.javions;

/** @author Nagyung Kim (339628)*/


public class WebMercator {


    /**
     * Calculate the x coordinate corresponding to the
     * given longitude (in radians) at the given zoom level
     * @param zoomlevel
     *          the zoomlevel
     * @param longitude
     *          the longitude
     * returns the x coordinate corresponding to the zoomlevel and longitude
     * @return x coordinate
     */

    public static double x(int zoomlevel, double longitude){
        double l = Units.convertTo(longitude, Units.Angle.RADIAN);
        double x = Math.pow(2, 8+zoomlevel) * (l/(2*Math.PI) + 0.5);
        return x ;
    }

    /**
     *Calculate the y-coordinate corresponding to the
     * given latitude (in radians) at the given zoom level
     * @param zoomlevel
     *          the zoomlevel
     * @param latitude
     *          the latitude
     *returns the y coordinate corresponding to the zoomlevel and latitude
     *@return y coordinate
     */

    public static double y(int zoomlevel, double latitude){
        double lat = Units.convertTo(latitude, Units.Angle.RADIAN);
        double y = Math.pow(2,8+zoomlevel)*((-Math2.asinh(Math.tan(lat)))/(2*Math.PI) + 0.5);
        return y ;

    }
}
