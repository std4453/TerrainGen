package terraingen.backend.commons;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * A box with a rectangular boundaries and contain points. The boundaries should be
 * immutable. Only points that locate in the boundaries may exist in the box.
 */
public class PointBox implements Iterable<Point> {
	protected Boundaries boundaries;
	protected List<Point> points;

	public PointBox(Boundaries boundaries, Point... points) {
		this(boundaries);
		this.addPoints(points);
	}

	public PointBox(Boundaries boundaries, List<Point> points) {
		this(boundaries);
		this.addPoints(points);
	}

	public PointBox(Boundaries boundaries) {
		this.boundaries = boundaries.copy();
		this.points = new Vector<>();
	}

	public void addPoints(Point... points) {
		for (Point point : points)
			if (this.boundaries.inBoundaries(point))
				this.points.add(point);
	}

	public Point getPoint(int n) {
		return (n >= 0 && n < this.pointCount()) ? this.points.get(n) : null;
	}

	public void addPoints(List<Point> points) {
		this.points.addAll(points);
	}

	public void removePoints(Point point) {
		this.points.remove(point);
	}

	public int pointCount() {
		return this.points.size();
	}

	public Boundaries getBoundaries() {
		return this.boundaries;
	}

	public List<Point> getPoints() {
		return this.points;
	}

	@Override
	public Iterator<Point> iterator() {
		return this.points.iterator();
	}
}
