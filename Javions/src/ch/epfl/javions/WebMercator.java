package ch.epfl.javions;

public class WebMercator {
    public double x(int zoomlevel, double logitude){

        double x = Math.pow(2, 8+zoomlevel) * (logitude/Math.PI + 1/2);
        return x ;
    }
}
