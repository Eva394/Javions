package ch.epfl.javions;

/**
 * @author Nagyung Kim (339628)
 * <p>
 * Define preconditions
 * <p>
 * Throws IllegalArgumentException when boolean shouldBeTrue is False
 * @throws IllegalArgumentException if boolean shouldBeTrue is False
 */

public final class Preconditions {

    private Preconditions() {
    }


    /**
     * Checks the condition <code>shouldBeTrue</code>
     * @param shouldBeTrue condition to check
     * @throws IllegalArgumentException if <code>shouldBeTrue</code> is false
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if ( !shouldBeTrue ) {
            throw new IllegalArgumentException();
        }
    }
}