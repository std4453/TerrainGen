package org.ajwerner.voronoi;

public class BreakPoint {
	private final Voronoi v;
	protected final Point s1, s2;
	protected VoronoiEdge e;
	private boolean isEdgeLeft;
	public final Point edgeBegin;

	private double cacheSweepLoc;
	private Point cachePoint;

	public BreakPoint(Point left, Point right, VoronoiEdge e, boolean isEdgeLeft,
					  Voronoi v) {
		this.v = v;
		this.s1 = left;
		this.s2 = right;
		this.e = e;
		this.isEdgeLeft = isEdgeLeft;
		this.edgeBegin = this.getPoint();
	}

	private static double sq(double d) {
		return d * d;
	}

	public void finish(Point vert) {
		if (this.isEdgeLeft) {
			this.e.p1 = vert;
		} else {
			this.e.p2 = vert;
		}
	}

	public void finish() {
		Point p = this.getPoint();
		this.finish(p);
	}

	public Point getPoint() {
		double l = this.v.getSweepLoc();
		if (l == this.cacheSweepLoc) {
			return this.cachePoint;
		}
		this.cacheSweepLoc = l;

		// FIXME: MY HACK
		double x1 = this.s1.x, x2 = this.s2.x;
		double y1 = this.s1.y, y2 = this.s2.y;
		double y0 = l;
		y1 = -y1;
		y2 = -y2;
		y0 = -y0;

		double x, y;
		if (y1 == y2) {
			x = (x1 + x2) / 2;
			y = (y0 + y1) / 2 - (y0 - y1) * sq(x1 - x2) / 8;
		} else {
			final double a = y1 - y2;
			final double b = 2 * ((y2 * x1 - y1 * x2) + y0 * (x2 - x1));
			final double c = (y0 - y1) * (y1 - y2) * (y2 - y0) + (sq(
					x2) * y1 - sq(x1) * y2) + y0 * (sq(x1) - sq(x2));
			final double delta = sq(b) - 4 * a * c;
			double sqrtDelta = delta < 1e-3 ? 0 : Math.sqrt(delta);
			sqrtDelta /= a * 2;
			final double axis = -b / a / 2;

			x = axis + sqrtDelta;
			y = (y1 + y2) / 2 + (x1 - x2) * (x1 + x2 - 2 * x) / 2 / (y1 - y2);
		}

		y = -y;

		this.cachePoint = new Point(x, y);
		return this.cachePoint;
	}

	public String toString() {
		return String.format("%s \ts1: %s\ts2: %s", this.getPoint(), this.s1, this.s2);
	}

	public VoronoiEdge getEdge() {
		return this.e;
	}
}
