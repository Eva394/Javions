package ch.epfl.javions;

/** @author Nagyung Kim (339628)
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