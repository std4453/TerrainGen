package terraingen.backend.commons;

/**
 * Lightweight POJO point class with simple utility methods.
 */
public class Point {
	public double x, y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point() {
		this(0, 0);
	}

	public Point(Point point) {
		this();
		this.set(point);
	}

	public double getX() {
		return this.x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return this.y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public Point copy() {
		return new Point(this);
	}

	public void set(Point point) {
		if (point != null) {
			this.x = point.x;
			this.y = point.y;
		}
	}
}
