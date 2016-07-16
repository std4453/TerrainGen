package terraingen.backend.commons.voronoi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.Point;
import terraingen.backend.commons.PointBox;

import java.util.List;
import java.util.Vector;

/**
 *
 */
public class VoronoiBox extends PointBox {
	private static final Log log = LogFactory.getLog(VoronoiBox.class);

	public static class Edge {
		public Cell cell1, cell2;
		public Point point1, point2;

		@Deprecated
		public Edge(Point point1, Point point2) {
			this.point1 = point1;
			this.point2 = point2;
		}

		public Edge(Cell cell1, Cell cell2) {
			this.cell1 = cell1;
			this.cell2 = cell2;

			cell1.edges.add(this);
			cell2.edges.add(this);
		}

		public void setPoint1(Point point1) {
			this.point1 = point1;
		}

		public void setPoint2(Point point2) {
			this.point2 = point2;
		}
	}

	public static class Cell {
		public List<Edge> edges;
		/**
		 * Vertices of the cell, already sorted for use in
		 * {@linkplain java.awt.Polygon Polygon}
		 */
		public List<Point> vertices;
		public Point site;

		public Cell(Point site) {
			this.site = site;

			this.edges = new Vector<>();
			this.vertices = new Vector<>();
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
