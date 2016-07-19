package terraingen.backend.commons.voronoi;

import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.Point;
import terraingen.backend.commons.PointBox;
import terraingen.utils.MathUtils;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 *
 */
public class VoronoiBox extends PointBox {
	public static class Edge {
		public Cell cell1, cell2;
		public Point point1, point2;

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
		/**
		 * Compare points in the cells according to their angle to the site
		 */
		public static class PointComparator implements Comparator<Point> {
			protected Point center;

			public PointComparator(Point center) {
				this.center = center;
			}

			@Override
			public int compare(Point o1, Point o2) {
				if (o1 == o2)
					return 0;
				double angle1 = MathUtils.angle(this.center, o1);
				double angle2 = MathUtils.angle(this.center, o2);
				return Double.compare(angle1, angle2);
			}
		}

		public Point site;

		public List<Edge> edges;
		/**
		 * Vertices of the cell, already sorted for use in
		 * {@linkplain Polygon Polygon}
		 */
		public Set<Point> vertices;

		public Cell(Point site) {
			this.site = site;

			this.edges = new Vector<>();
			this.vertices = new TreeSet<>(new PointComparator(site));
		}

		public void generatePoints() {
			for (Edge edge : this.edges) {
				this.vertices.add(edge.point1);
				this.vertices.add(edge.point2);
			}
		}
	}

	protected List<Edge> edges;
	protected List<Cell> cells;
	protected List<Point> voronoiPoints;

	/**
	 * For performance reason, {@code edges}, {@code cells} and {@code points} are
	 * simply assigned to the corresponding fields in the instance ( instead of copying
	 * the values )
	 *
	 * @param boundaries
	 * 		Boundaries
	 * @param sites
	 * 		Sites
	 * @param edges
	 * 		Edges
	 * @param cells
	 * 		Cells, whose sites are those in {@code sites}
	 * @param points
	 * 		Voronoi points ( intersections of edges )
	 */
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
