package org.ajwerner.voronoi;

import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.Point;
import terraingen.backend.commons.PointBox;
import terraingen.backend.commons.voronoi.VoronoiBox;
import terraingen.backend.nodegraph.IProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 *
 */
public class VoronoiAdapter implements IProcessor<PointBox, VoronoiBox> {
	@Override
	public VoronoiBox process(PointBox input) {
		List<terraingen.backend.commons.Point> points = input.getPoints();
		ArrayList<org.ajwerner.voronoi.Point> points2 = new ArrayList<>();
		for (Point point : points)
			points2.add(new org.ajwerner.voronoi.Point(point.x, point.y));
		Voronoi voronoi = new Voronoi(points2);

		Boundaries boundaries = input.getBoundaries();
		List<VoronoiBox.Cell> cells = new Vector<>();
		for (Point point : points)
			cells.add(new VoronoiBox.Cell(point));
		List<Point> voronoiPoints = new Vector<>();
//		for (BreakPoint breakPoint : voronoi.breakPoints) {
//			voronoiPoints.add(new Point(breakPoint.);
//		}
		List<VoronoiBox.Edge> edges = new Vector<>();
		for (VoronoiEdge edge : voronoi.edgeList)
			edges.add(new VoronoiBox.Edge(new Point(edge.p1.x, edge.p1.y), new Point(edge
					.p2.x, edge.p2.y)));
		VoronoiBox voronoiBox = new VoronoiBox(boundaries, points, edges,
				cells, voronoiPoints);
		return voronoiBox;
	}
}
