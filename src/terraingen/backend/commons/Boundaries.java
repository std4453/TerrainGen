package terraingen.backend.commons;

/**
 * Lightweight POJO class for rectangular boundaries
 */
public class Boundaries {
	public double top, bottom, left, right;

	public Boundaries(double top, double bottom, double left, double right) {
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
	}

	public Boundaries(double width, double height) {
		this(0, height, 0, width);
	}

	public Boundaries() {
		this(0, 0, 0, 0);
	}

	public Boundaries(Boundaries boundaries) {
		this();
		this.set(boundaries);
	}

	public double getTop() {
		return this.top;
	}

	public void setTop(double top) {
		this.top = top;
	}

	public double getBottom() {
		return this.bottom;
	}

	public void setBottom(double bottom) {
		this.bottom = bottom;
	}

	public double getLeft() {
		return this.left;
	}

	public void setLeft(double left) {
		this.left = left;
	}

	public double getRight() {
		return this.right;
	}

	public void setRight(double right) {
		this.right = right;
	}

	public Boundaries copy() {
		return new Boundaries(this);
	}

	public void set(Boundaries boundaries) {
		if (boundaries != null) {
			this.top = boundaries.top;
			this.bottom = boundaries.bottom;
			this.left = boundaries.left;
			this.right = boundaries.right;
		}
	}

	public boolean inBoundaries(Point point) {
		return point.x >= this.left && point.x <= this.right && point.y >= this.top &&
				point.y <= this.bottom;
	}

	public Boundaries expend(double n) {
		return new Boundaries(this.top - n, this.bottom + n, this.left - n,
				this.right + n);
	}

	public Point center() {
		return this.point(.5d, .5d);
	}

	/**
	 * Get linear interpolated point in {@code Boundaries}
	 */
	public Point point(double u, double v) {
		return new Point(this.left * (1 - u) + this.right * u,
				this.top * (1 - v) + this.bottom * v);
	}
}
