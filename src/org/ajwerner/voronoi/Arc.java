package org.ajwerner.voronoi;

public class Arc extends ArcKey {
	private final Voronoi v;
	public BreakPoint left, right;
	public final Point site;

	public Arc(BreakPoint left, BreakPoint right, Voronoi v) {
		this.v = v;
		if (left == null && right == null) {
			throw new RuntimeException("cannot make arc with null breakpoints");
		}
		this.left = left;
		this.right = right;
		this.site = (left != null) ? left.s2 : right.s1;
	}

	public Arc(Point site, Voronoi v) {
		// Only for creating the first org.ajwerner.voronoi.Arc
		this.v = v;
		this.left = null;
		this.right = null;
		this.site = site;
	}

	protected Point getRight() {
		if (this.right != null) return this.right.getPoint();
		return new Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	protected Point getLeft() {
		if (this.left != null) return this.left.getPoint();
		return new Point(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	public Point checkCircle() {
		if ((this.left == null) || (this.right == null)) return null;
		if (Point.ccw(this.left.s1, this.site, this.right.s2) != -1) return null;
//		return (this.left.getEdge().intersection(this.right.getEdge()));

		// FIXME: MY HACK
		final Point s1 = this.left.s1;
		final Point s2 = this.site;
		final Point s3 = this.right.s2;
		final double x1 = s1.x, x2 = s2.x, x3 = s3.x;
		final double y1 = s1.y, y2 = s2.y, y3 = s3.y;
		final double
				a = x2 - x1,
				b = y2 - y1,
				c = x3 - x2,
				d = y3 - y2,
				e = (y2 * y2 - y1 * y1 + x2 * x2 - x1 * x1) / 2,
				f = (y3 * y3 - y2 * y2 + x3 * x3 - x2 * x2) / 2;
		final double epsilon = 1e-3;
		final double matD = a * d - c * b;

		if (Math.abs(matD) < epsilon)    // no solution
			return null;
		final double x = (e * d - f * b) / matD;
		final double y = (a * f - c * e) / matD;
		// ( x , y ) is the circle center

		return new Point(x, y);
	}
}