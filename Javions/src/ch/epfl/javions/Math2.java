package ch.epfl.javions;

/** @author Nagyung Kim (339628) */

public final class Math2 {
    private Math2() {}

    /**
     * Restricts the range of value v in between min and max
     * and gives min when v is smaller than v
     * and gives max when v is bigger than v
     *
     * @param min
     *          the minimum value that v can be
     * @param v
     *          value v
     * @param max
     *          the maximum value that v can be
     * @throws IllegalArgumentException
     *          if the int min is bigger than int max
     *
     *
     * return the restricted v value
     * which is shown as min if v is inferior to min v
     * and shown as max if v is superior to max v
     * and value v if it is in range
     * @return the restricted v value
     */


    public static int clamp(int min, int v, int max) {

        if (min > max) {
            throw new IllegalArgumentException();
        }

        else {

            v = Math.min(v, max);
            v = Math.max(v, min);


            return v;
        }
    }

    /**
     * calculate arsinh(x) vlaue
     * @param x
     *          a number in format double
     * returns the calculated arsinh(x) value
     * @return arsinh(x) value
     */

    public static double asinh(double x){

        return Math.log(x + Math.sqrt(Math.pow(x, 2) + 1));
    }

}
