package terraingen.utils;

import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.Point;

/**
 * Utility class for simple math calculations ( because {@link Math} doesn't provide
 * some simple calculations, like calculation the square of an double or rounding a
 * double to an int ), methods here should be imported statically for easy use.
 */
public class MathUtils {
	public static final double eps = 1e-7;

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

	public static double max(double a, double b) {
		return a > b ? a : b;
	}

	public static double min(double a, double b) {
		return a < b ? a : b;
	}

	/**
	 * Calculates the angle of {@code point} to {@code center}
	 *
	 * @param center
	 * 		center point
	 * @param point
	 * 		target point
	 *
	 * @return angle, in radians, ranges from {@code -pi} to {@code pi}
	 * @see Math#atan2(double, double)
	 */
	public static double angle(Point center, Point point) {
		double dx = point.x - center.x, dy = point.y - center.y;
		return Math.atan2(dy, dx);
	}

	public static Point transform(Boundaries original, Point point, Boundaries target) {
		double oWidth = original.right - original.left;
		double oHeight = original.bottom - original.top;
		double tWidth = target.right - target.left;
		double tHeight = target.bottom - target.top;
		return new Point((point.x - original.left) / oWidth * tWidth + target.left,
				(point.y - original.top) / oHeight * tHeight + target.top);
	}

	public static int floor(double n) {
		return n > 0 ? (int) n : (int) n - 1;
	}

	public static double dot(double n[], double x, double y) {
		return n[0] * x + n[1] * y;
	}
}
