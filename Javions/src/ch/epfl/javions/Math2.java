package ch.epfl.javions;

/**
 * Defining Clamp and asinh
 * @author Nagyung Kim (339628)
 */

public final class Math2 {

    private Math2() {
    }


    /**
     * Restricts the range of value v in between min and max and gives min when v is smaller than v and gives max when v
     * is bigger than v (in int)
     * @param min the minimum value that v can be
     * @param v   value v
     * @param max the maximum value that v can be
     * @return the restricted v value
     * @throws IllegalArgumentException if the int min is bigger than int max return the restricted v value which is
     *                                  shown as min if v is inferior to min v and shown as max if v is superior to max
     *                                  v and value v if it is in range
     */
    public static int clamp(int min, int v, int max) {
        Preconditions.checkArgument( min <= max );

        return Math.max( Math.min( v, max ), min );
    }

    /**
     *  Restricts the range of value v in between min and max and gives min when v is smaller than v and gives max when v
     *  is bigger than v (in double)
     * @param min the minimum value that v can be
     * @param v value v
     * @param max he maximum value that v can be
     * @return he restricted v value
     *@throws IllegalArgumentException if the int min is bigger than int max return the restricted v value which is
     *                                  shown as min if v is inferior to min v and shown as max if v is superior to max
     *                                  v and value v if it is in range
     */

    public static double clamp(double min, double v, double max){
        Preconditions.checkArgument( min <= max );

        return Math.max( Math.min( v, max ), min );
    }


    /**
     * calculate arsinh(x) vlaue
     * @param x a number in format double returns the calculated arsinh(x) value
     * @return arsinh(x) value
     */
    public static double asinh(double x) {

        return Math.log( x + Math.hypot( x, 1d ) );
    }
}
