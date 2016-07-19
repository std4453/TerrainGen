package terraingen.backend.commons;

import terraingen.backend.commons.voronoi.VoronoiBox;
import terraingen.backend.nodegraph.IProcessor;

import java.util.*;

import static terraingen.utils.MathUtils.max;
import static terraingen.utils.MathUtils.min;

public class Lloyd implements IProcessor<VoronoiBox, PointBox> {
	@Override
	public PointBox process(VoronoiBox input) {
		Boundaries boundaries = input.boundaries;
		List<Point> points = new Vector<>();

		for (VoronoiBox.Cell cell : input.getCells())
			points.add(evaluateCentroid(boundaries, cell));
		return new PointBox(boundaries, points);
	}

	protected Point evaluateCentroid(Boundaries boundaries, VoronoiBox.Cell cell) {
		Collection<VoronoiBox.Edge> edges = cell.edges;
		Point center = cell.site;
		Set<Point> points = new TreeSet<>(new VoronoiBox.Cell.PointComparator(center));

		for (VoronoiBox.Edge edge : edges)
			intersect(points, boundaries, edge);

		double x = 0, y = 0;
		for (Point point : points) {
			x += point.x;
			y += point.y;
		}
		int size = points.size();
		return new Point(x / size, y / size);
	}

	protected boolean insideBoundaries(Boundaries boundaries, Point point) {
		return point.x <= boundaries.right && point.x >= boundaries.left && point.y <=
				boundaries.bottom && point.y >= boundaries.top;
	}

	protected void intersect(Set<Point> points, Boundaries boundaries, VoronoiBox
			.Edge edge) {
		Point p1 = edge.point1;
		Point p2 = edge.point2;
		boolean p1Inside = insideBoundaries(boundaries, p1);
		boolean p2Inside = insideBoundaries(boundaries, p2);

		if (p1Inside)
			points.add(p1);
		if (p2Inside)
			points.add(p2);
		if (p1Inside && p2Inside)
			return;

		final double x1 = p1.x, x2 = p2.x;
		final double y1 = p1.y, y2 = p2.y;
		final double minx = min(x1, x2), maxx = max(x1, x2);
		final double miny = min(y1, y2), maxy = max(y1, y2);
		final double x1mx2 = x1 - x2, y1my2 = y1 - y2;
		final double kx = y1my2 / x1mx2, ky = x1mx2 / y1my2;
		final double bx = (x1 * y2 - x2 * y1) / x1mx2, by = (y1 * x2 - y2 * x1) / y1my2;

		double x, y;
		// top edge
		x = boundaries.top * ky + by;
		if (x >= minx && x <= maxx && x >= boundaries.left && x <= boundaries.right)
			points.add(new Point(x, boundaries.top));
		// bottom edge
		x = boundaries.bottom * ky + by;
		if (x >= minx && x <= maxx && x >= boundaries.left && x <= boundaries.right)
			points.add(new Point(x, boundaries.bottom));
		// left edge
		y = boundaries.left * kx + bx;
		if (y >= miny && y <= maxy && y >= boundaries.top && y <= boundaries.bottom)
			points.add(new Point(boundaries.left, y));
		// right edge
		y = boundaries.right * kx + bx;
		if (y >= miny && y <= maxy && y >= boundaries.top && y <= boundaries.bottom)
			points.add(new Point(boundaries.right, y));
	}
}
