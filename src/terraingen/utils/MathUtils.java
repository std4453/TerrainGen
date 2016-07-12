package terraingen.utils;

/**
 * Utility class for simple math calculations ( because {@link Math} doesn't provide
 * some simple calculations, like calculation the square of an double or rounding a
 * double to an int ), methods here should be imported statically for easy use.
 */
public class MathUtils {
	public static double square(double n) {
		return n * n;
	}

	public static double abs(double n) {
		return n < 0 ? -n : n;
	}

	public static double sqrt(double n) {
		return Math.sqrt(n);
	}

	public static int round(double n) {
		return (int) Math.round(n);
	}
}
