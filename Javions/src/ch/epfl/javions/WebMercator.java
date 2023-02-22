package ch.epfl.javions;

public class WebMercator {
    public static double x(int zoomlevel, double longitude){

        double l = Units.convertTo(longitude, Units.Angle.RADIAN);
        double x = Math.pow(2, 8+zoomlevel) * (l/(2*Math.PI) + 0.5);
        return x ;
    }

    public static double y(int zoomlevel, double latitude){

        double lat = Units.convertTo(latitude, Units.Angle.RADIAN);
        double y = Math.pow(2,8+zoomlevel)*((-Math2.asinh(Math.tan(lat)))/(2*Math.PI) + 0.5);
        return y ;

    }
}
