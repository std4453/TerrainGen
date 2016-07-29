package terraingen.backend.algorithms.amitp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import terraingen.backend.commons.Point;
import terraingen.backend.commons.voronoi.VoronoiBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Map used in the algorithm described
 * <a href="http://www-cs-students.stanford.edu/~amitp/game-programming/polygon-map-generation/">here</a>,
 * not to confuse it with {@link java.util.Map}.
 */
public class Map {
	private static final Log log = LogFactory.getLog(Map.class);

	/**
	 * A {@code Corner} represents in the same time a voronoi point and a triangle in a
	 * <a href="https://en.wikipedia.org/wiki/Delaunay_triangulation"><i>Delaunay
	 * Graph</i></a>. The 3 {@linkplain Center Centers} are the vertices of the
	 * <i>Delaunay triangle</i>.<br /><br />
	 * According to the implementation in {@link #Map(VoronoiBox)}, {@code s1} and
	 * {@code s2} are {@linkplain Center Centers} of {@code e1}, similar to {@code e2}
	 * and {@code e3} as well. Though I don't know the use of this for now, it may
	 * become helpful in the future.
	 */
	protected static class Corner {
		/**
		 * Additional data
		 */
		public java.util.Map<String, Object> data = new HashMap<>();

		public Edge e1, e2, e3;
		/**
		 * Lazy initialized
		 */
		public Center s1, s2, s3;

		/**
		 * Position of {@code Corner}
		 */
		public Point point;

		public Corner(Edge e1, Edge e2, Edge e3, Point point) {
			this.e1 = e1;
			this.e2 = e2;
			this.e3 = e3;
			this.point = point;
		}

		public void setS1(Center s1) {
			this.s1 = s1;
		}

		public void setS2(Center s2) {
			this.s2 = s2;
		}

		public void setS3(Center s3) {
			this.s3 = s3;
		}

		/**
		 * Convenient method that sets all vertices at once.
		 */
		public void setCenters(Center s1, Center s2, Center s3) {
			this.s1 = s1;
			this.s2 = s2;
			this.s3 = s3;
		}
	}

	/**
	 * A {@code Center} is a more a point than a
	 * {@linkplain terraingen.backend.commons.voronoi.VoronoiBox.Cell Cell}, though it
	 * forms a one-to-one correspondence with
	 * {@linkplain terraingen.backend.commons.voronoi.VoronoiBox.Cell Cells}. Named
	 * according to the post of the algorithm.
	 */
	protected static class Center {
		/**
		 * Additional data
		 */
		public java.util.Map<String, Object> data = new HashMap<>();

		public List<Edge> edges;
		public List<Corner> corners;

		/**
		 * Site point of cell.
		 */
		public Point point;

		public Center(Point point) {
			this.point = point;

			this.edges = new ArrayList<>();
			this.corners = new ArrayList<>();
		}
	}

	protected static class Edge {
		/**
		 * Additional data
		 */
		public java.util.Map<String, Object> objects = new HashMap<>();

		public Center s1, s2;
		/**
		 * Lazy initialized ( because {@linkplain Corner Corners} are initialized after
		 * {@code Edges} )
		 */
		public Corner c1, c2;

		public Edge(Center s1, Center s2) {
			this.s1 = s1;
			this.s2 = s2;
		}

		public void setC1(Corner c1) {
			this.c1 = c1;
		}

		public void setC2(Corner c2) {
			this.c2 = c2;
		}

		/**
		 * Sets the next {@code null} {@link Corner}
		 *
		 * @param corner
		 * 		Corner
		 */
		public void setCorner(Corner corner) {
			if (this.c1 == null)
				this.c1 = corner;
			else if (this.c2 == null)
				this.c2 = corner;
		}

		/**
		 * Gets the other {@linkplain Center Center} of the {@linkplain Edge Edge}
		 */
		public Center otherCenter(Center center) {
			return this.s1 == center ? this.s2 : this.s2 == center ? this.s1 : null;
		}
	}

	protected List<Center> centers;
	protected List<Corner> corners;
	protected List<Edge> edges;

	/**
	 * Only initialize instance fields.
	 */
	private Map() {
		this.centers = new ArrayList<>();
		this.corners = new ArrayList<>();
		this.edges = new ArrayList<>();
	}

	/**
	 * Takes a {@link VoronoiBox} as input and construct the data structure of the map.
	 *
	 * @param voronoiBox
	 * 		The input
	 */
	public Map(VoronoiBox voronoiBox) {
		this();

		// fast lookup maps
		java.util.Map<VoronoiBox.Cell, Center> cell2center = new HashMap<>();
		java.util.Map<Point, List<Edge>> corner2edge = new HashMap<>();

		// add all cells
		for (VoronoiBox.Cell cell : voronoiBox.getCells()) {
			Center center = new Center(cell.site);
			this.centers.add(center);
			cell2center.put(cell, center);
		}

		// add edges
		for (VoronoiBox.Edge edge : voronoiBox.getEdges()) {
			Center s1 = cell2center.get(edge.cell1), s2 = cell2center.get(edge.cell2);
			if (s1 == null || s2 == null) {
				log.debug("Edge don't have 2 adjacent cells.");
				continue;
			}

			Edge edge2 = new Edge(s1, s2);
			s1.edges.add(edge2);
			s2.edges.add(edge2);
			Point c1 = edge.point1, c2 = edge.point2;
			if (!corner2edge.containsKey(c1))
				corner2edge.put(c1, new ArrayList<>());
			if (!corner2edge.containsKey(c2))
				corner2edge.put(c2, new ArrayList<>());
			corner2edge.get(c1).add(edge2);
			corner2edge.get(c2).add(edge2);
			this.edges.add(edge2);
		}

		// add corners
		for (java.util.Map.Entry<Point, List<Edge>> entry : corner2edge.entrySet()) {
			List<Edge> edgesList = entry.getValue();
			if (edgesList.size() < 3) {
				log.debug("Corner have less than 3 edges connected.");
				continue;
			}

			Edge e1 = edgesList.get(0), e2 = edgesList.get(1), e3 = edgesList.get(2);
			Corner corner = new Corner(e1, e2, e3, entry.getKey());

			// for meaning of this line see documentation of Corner
			Center s1 = intersection(e2, e3),
					s2 = intersection(e1, e3),
					s3 = intersection(e1, e2);
			if (s1 == null || s2 == null || s3 == null) {
				log.error("Cells of edges to corner don't intersect.");
				continue;
			}
			e1.setCorner(corner);
			e2.setCorner(corner);
			e3.setCorner(corner);
			corner.setCenters(s1, s2, s3);
			s1.corners.add(corner);
			s2.corners.add(corner);
			s3.corners.add(corner);
			this.corners.add(corner);
		}
	}

	/**
	 * Returns the same {@linkplain Center Center} which the two {@linkplain Edge Edges}
	 * possess at the same time, if, there is such one.
	 */
	private Center intersection(Edge e1, Edge e2) {
		Center s11 = e1.s1, s12 = e1.s2, s21 = e2.s1, s22 = e2.s2;
		return s11 == s21 ? s11 :
				s11 == s22 ? s11 :
						s12 == s21 ? s12 :
								s12 == s22 ? s12 :
										null;
	}
}
