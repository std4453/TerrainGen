package terraingen.backend.commons.voronoi;

import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.Point;
import terraingen.backend.commons.PointBox;

import java.util.List;

/**
 *
 */
public class VoronoiBox extends PointBox {
	public static class Edge {
		public Cell cell1, cell2;
		public Point point1, point2;

		public Edge(Point point1, Point point2) {
			this.point1 = point1;
			this.point2 = point2;
		}
	}

	public static class Cell {
		public List<Edge> edges;
		public Point site;

		public Cell(Point site) {
			this.site = site;
		}
	}

	protected List<Edge> edges;
	protected List<Cell> cells;
	protected List<Point> voronoiPoints;

	public VoronoiBox(Boundaries boundaries, List<Point> sites, List<Edge> edges,
					  List<Cell> cells, List<Point> points) {
		super(boundaries, sites);
		this.edges = edges;
		this.cells = cells;
		this.voronoiPoints = points;
	}

	public List<Edge> getEdges() {
		return this.edges;
	}

	public List<Cell> getCells() {
		return this.cells;
	}

	public List<Point> getVoronoiPoints() {
		return this.voronoiPoints;
	}
}