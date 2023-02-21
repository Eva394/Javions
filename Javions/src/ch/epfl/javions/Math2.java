package ch.epfl.javions;

public final class Math2 {
    private Math2() {}

    public static int clamp(int min, int v, int max) {

        if (min > max) {
            throw new IllegalArgumentException();
        }

        else {

            v = Math.min(v,max);
            v = Math.max(v, min);


            return v;
        }
    }

    public static double asinh(double x){
        return Math.log(x + Math.sqrt(Math.pow(x, 2) + 1));
    }

}
