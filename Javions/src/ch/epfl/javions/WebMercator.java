package ch.epfl.javions;

public class WebMercator {
    public double x(int zoomlevel, double logitude){

        double x = Math.pow(2, 8+zoomlevel) * (logitude/Math.PI + 1/2);
        return x ;
    }

    public double y(int zoomlevel, double latitude){

        double y = Math.pow(2,8+zoomlevel)*(-Math2.asinh(Math.tan(latitude)))/2*Math.PI + 1/2;

    }
}
