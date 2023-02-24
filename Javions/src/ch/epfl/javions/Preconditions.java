package ch.epfl.javions;

/** @author Nagyung Kim (339628)
 * Throws IllegalArgumentException when boolean shouldBeTrue is False
 * @throws IllegalArgumentException
 * 			if boolean shouldBeTrue is False
 */

public final class Preconditions {
	private Preconditions() {}
	public static void checkArgument(boolean shouldBeTrue){
		if (! shouldBeTrue){
			throw new IllegalArgumentException();
		}
	}
}