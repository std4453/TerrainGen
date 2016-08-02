package terraingen.backend.commons.voronoi;

import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.Point;
import terraingen.backend.commons.PointBox;
import terraingen.backend.nodegraph.IProcessor;

import java.util.*;

import static terraingen.utils.MathUtils.abs;
import static terraingen.utils.MathUtils.eps;
import static terraingen.utils.MathUtils.square;

/**
 * Implementation of the Fortune's Algorithm, see
 * <a href="https://en.wikipedia.org/wiki/Fortune's_Algorithm"><i>Fortune's
 * Algorithm on Wikipedia</i></a>.
 */
public class Fortune implements IProcessor<PointBox, VoronoiBox> {
	/**
	 * Arc describes a section of a parabola, with two breakpoints ( could be null )
	 * on its sides. While arcs are contained in a {@link TreeMap}, they are prepared
	 * according to their midpoint's x coord.<br />
	 * Arc's subclass {@link ArcQuery} is used to search for the Arc above the site
	 * event, while {@code compareTo()} finds the arc that "contains" the x position of
	 * the query.<br /><br />
	 * The left and right points of {@code Arc} can be modified, and are modified only
	 * when a <i>circle event</i> happens. When a <i>site event</i> happens, the {@code
	 * Arc} above the site is actually removed and three new fill in its space.
	 */
	protected static class Arc implements Comparable<Arc> {
		/**
		 * could be {@code null} when a side of this {@code Arc} extends to infinity (
		 * when there is no {@code Arc} there to intersect )
		 */
		public BreakPoint left, right;
		public Point site;
		/**
		 * null if this {@code Arc} doesn't have an circle event, either there wouldn't
		 * one or the one of this {@code Arc} is removed.
		 */
		public CircleEvent circleEvent;

		public Arc(BreakPoint left, BreakPoint right, Point site) {
			this.left = left;
			this.right = right;
			this.site = site;
		}

		public double getLeftX() {
			return this.left != null ? this.left.getPoint().x : Double.NEGATIVE_INFINITY;
		}

		public double getRightX() {
			return this.right != null ? this.right.getPoint().x : Double.POSITIVE_INFINITY;
		}

		/**
		 * Compares two arcs.<br />
		 * {@code Arc a} is same to {@code Arc b}, if and only if:<br />
		 * <ol>
		 * <li>{@code a} is the same object with {@code b}</li>
		 * <li>{@code a} is not {@link ArcQuery} and {@code b} is {@link ArcQuery}
		 * and range described by {@code a} contains query of {@code b}</li>
		 * <li>{@code a} and {@code b} are both instances of {@link ArcQuery} and
		 * they have the same query ( not supposed to happen )</li>
		 * <li>Range described by {@code a} has the same midpoint with that of
		 * {@code b} ( not supposed to happen )</li>
		 * </ol>
		 *
		 * @param o
		 * 		The other Arc
		 *
		 * @return Compare result
		 */
		@Override
		public int compareTo(Arc o) {
			if (this == o)    // same object
				return 0;

			if (this instanceof ArcQuery && o instanceof ArcQuery)
				return Double.compare(((ArcQuery) this).query, ((ArcQuery) o).query);
			if (this instanceof ArcQuery)
				return -o.compareTo(this);
			double
					tl = this.getLeftX(),
					tr = this.getRightX(),
					ol = o.getLeftX(),
					or = o.getRightX();
			if (o instanceof ArcQuery && tl <= ol && tr >= or)    // contains the query
				return 0;
			// quick and simple cases
			if (tr <= ol)
				return -1;
			if (tl >= or)
				return 1;

			return Double.compare(tl + tr, ol + or);
		}

		public void setCircleEvent(CircleEvent circleEvent) {
			this.circleEvent = circleEvent;
		}

		public void setLeft(BreakPoint left) {
			this.left = left;
		}

		public void setRight(BreakPoint right) {
			this.right = right;
		}
	}

	/**
	 * Used while a site event occurs and finds the {@link Arc} above the site
	 */
	protected static class ArcQuery extends Arc {
		public double query;

		public ArcQuery(double query) {
			super(null, null, null);
			this.query = query;
		}

		@Override
		public double getLeftX() {
			return this.query;
		}

		@Override
		public double getRightX() {
			return this.query;
		}
	}

	/**
	 * A {@code BreakPoint} is the intersection of two {@link Arc}{@code s}. The
	 * existence of a {@code BreakPoint} only means that two {@link Arc}{@code s} on
	 * the beach line will intersect, while its position is dynamic within the
	 * execution of the <i>Fortune's Algorithm</i> and only determined when its {@code
	 * finish()} method is invoked ( when an <i>circle event</i> occurs or the whole
	 * process is finished and all {@code BreakPoints} are finished ). The trail of a
	 * {@code BreakPoint} ( from created to finished ) forms an edge in the generated
	 * voronoi map. Also the position of an {@code BreakPoint} is evaluated dynamically
	 * according the {@link Arc}{@code s} and the position of the sweep line.<br /><br />
	 * The {@link Arc}{@code s} of the {@code BreakPoint} are mutable and are modified
	 * only when an site event occurs.
	 */
	protected static class BreakPoint {
		/**
		 * Used to access the position of the sweep line
		 */
		public Context context;
		public Arc left, right;
		public Point finalPoint;
		public Point beginPoint;

		/**
		 * Edge of the voronoi box
		 */
		public VoronoiBox.Edge edge;
		/**
		 * True when the final point of the {@code BreakPoint} is assigned to the first
		 * point of the
		 * {@linkplain terraingen.backend.commons.voronoi.VoronoiBox.Edge Edge}
		 */
		public boolean point1;

		/**
		 * Point that stay unchanged when the sweep line stays still. A cache is built
		 * so that calculations go faster.
		 */
		protected Point cachedPoint;
		protected double cachedSweepLine = Double.NEGATIVE_INFINITY;

		public BreakPoint(Context context, Arc left, Arc right) {
			this(context);
			this.left = left;
			this.right = right;
			this.finalPoint = null;
		}

		/**
		 * Set the edge and point assignment of the {@code BreakPoint}, should be
		 * called before {@code finish()} of this {@code BreakPoint} is called
		 *
		 * @param edge
		 * 		The edge
		 * @param point1
		 * 		Whether the final point should be bound to the first point of
		 * 		the	{@linkplain terraingen.backend.commons.voronoi.VoronoiBox.Edge Edge}
		 */
		public void setEdge(VoronoiBox.Edge edge, boolean point1) {
			this.edge = edge;
			this.point1 = point1;
		}

		/**
		 * In case {@link Arc} requires its {@code BreakPoint}{@code s} to be
		 * instantiated first.
		 */
		public BreakPoint(Context context) {
			this.context = context;
		}

		public void setLeft(Arc left) {
			this.left = left;
		}

		public void setRight(Arc right) {
			this.right = right;
		}

		/**
		 * calculates the current position of the {@code BreakPoint}
		 *
		 * @return the {@code BreakPoint}'s current position
		 */
		public Point getPoint() {
			// resolve cache
			final double y0 = this.context.sweepLine;
			if (y0 == this.cachedSweepLine)
				return this.cachedPoint;

			final Point s1 = this.left.site;
			final Point s2 = this.right.site;
			final double x1 = s1.x, x2 = s2.x;
			final double y1 = s1.y, y2 = s2.y;

			final double y0my1 = y0 - y1,
					y2my0 = y2 - y0;

			double x, y;
			if (abs(y1 - y2) < eps) {
				// when site points of two parabolas have the same y coord then there's
				// only one intersection point, so it is solved to reduce calculations
				x = (x1 + x2) / 2;
				if (abs(y0my1) < eps)
					y = Double.NEGATIVE_INFINITY;
				else
					y = (y0 + y1) / 2 - square(x1 - x2) / 8 / y0my1;
			} else {
				// math solve of intersection of two parabolas
				final double a = y1 - y2;
				final double bDiv2 = y2my0 * x1 + y0my1 * x2;
				final double c = (y0my1 * a - square(x1)) * y2my0 - y0my1 * square(x2);
				final double deltaDiv4 = square(bDiv2) - a * c;
				double sqrtDeltaDiv2 = deltaDiv4 < eps ? 0 : Math.sqrt(deltaDiv4);
				x = (sqrtDeltaDiv2 - bDiv2) / a;
				y = (y1 + y2 + (x1 - x2) * (x1 + x2 - x - x) / a) / 2;
			}

			this.cachedPoint = new Point(x, y);
			this.cachedSweepLine = y0;
			return this.cachedPoint;
		}

		public void finish() {
			finish(this.getPoint());
		}

		public void finish(Point point) {
			this.finalPoint = point;
			if (this.point1)
				this.edge.setPoint1(this.finalPoint);
			else
				this.edge.setPoint2(this.finalPoint);
		}
	}

	/**
	 * Subclass of {@link CircleEvent} and {@link SiteEvent}, only to enable them be
	 * contained within one single {@link PriorityQueue}.
	 */
	protected static abstract class Event implements Comparable<Event> {
		public boolean isSiteEvent() {
			return this instanceof SiteEvent;
		}

		public boolean isCircleEvent() {
			return this instanceof CircleEvent;
		}

		public double getY() {
			return this.getPoint().y;
		}

		@Override
		public int compareTo(Event o) {
			if (this == o)
				return 0;
			return Double.compare(this.getY(), o.getY());
		}

		public abstract Point getPoint();
	}

	protected static class SiteEvent extends Event {
		public Point site;

		public SiteEvent(Point site) {
			this.site = site;
		}

		@Override
		public Point getPoint() {
			return this.site;
		}
	}

	protected static class CircleEvent extends Event {
		public Arc target;
		public Point position;

		public CircleEvent(Arc target, Point position) {
			this.target = target;
			this.position = position;
		}

		@Override
		public Point getPoint() {
			return this.position;
		}
	}

	/**
	 * Context of the current execution of "Fortune's Algorithm" ( in case {@code
	 * Fortune.process()} is invoked multi-threaded ). It provides information of the
	 * current state. A new instance of {@code Context} is created every time {@code
	 * Fortune.process()} is invoked.
	 */
	protected static class Context {
		// original content
		public PointBox pointBox;
		public Boundaries boundaries;
		public List<Point> points;

		// fortune's algorithm
		public double sweepLine;
		public TreeSet<Arc> beachLine;
		public PriorityQueue<Event> eventQueue;
		public HashSet<BreakPoint> breakPoints;

		// voronoi map
		public Map<Point, VoronoiBox.Cell> cells;
		public List<Point> voronoiPoints;
		public List<VoronoiBox.Edge> edges;

		public Context(PointBox pointBox) {
			// process input
			this.pointBox = pointBox;
			this.boundaries = pointBox.getBoundaries();
			this.points = pointBox.getPoints();

			// initialize data structures
			this.beachLine = new TreeSet<>();
			this.eventQueue = new PriorityQueue<>();
			this.breakPoints = new HashSet<>();

			// data for voronoi box
			this.cells = new HashMap<>();
			this.voronoiPoints = new Vector<>();
			this.edges = new Vector<>();
		}

		public void setSweepLine(double sweepLine) {
			this.sweepLine = sweepLine;
		}

	}

	@Override
	public VoronoiBox process(PointBox input) {
		Context context = new Context(input);

		preProcess(context);

		List<Point> points = context.points;
		PriorityQueue<Event> eventQueue = context.eventQueue;
		// add all site events to queue
		for (Point point : points)
			eventQueue.offer(new SiteEvent(point));
		// loop
		while (!eventQueue.isEmpty()) {
			Event event = eventQueue.poll();
			context.setSweepLine(event.getY());

			if (event.isSiteEvent())
				this.handleSiteEvent(context, (SiteEvent) event);
			else
				this.handleCircleEvent(context, (CircleEvent) event);
		}

		return postProcess(context);
	}

	/**
	 * Pre-process stage: initialize voronoi map
	 *
	 * @param context
	 * 		Current Context
	 */
	protected void preProcess(Context context) {
		List<Point> points = context.points;
		// add all cells
		for (Point point : points)
			context.cells.put(point, new VoronoiBox.Cell(point));
	}

	/**
	 * Post-process stage: construct {@link VoronoiBox}
	 *
	 * @param context
	 * 		Current context
	 *
	 * @return Constructed {@link VoronoiBox}
	 */
	protected VoronoiBox postProcess(Context context) {
		List<VoronoiBox.Cell> cells = new Vector<>();
		cells.addAll(context.cells.values());

		for (BreakPoint breakPoint : context.breakPoints) {
			if (breakPoint.finalPoint == null) {
				breakPoint.finish();
				context.voronoiPoints.add(breakPoint.finalPoint);
			}
		}

		for (VoronoiBox.Cell cell : cells)
			cell.generatePoints();

		return new VoronoiBox(context.boundaries, context.points, context.edges,
				cells,
				context.voronoiPoints);
	}

	/**
	 * Handle <i>site event</i>
	 *
	 * @param context
	 * 		Current context
	 * @param event
	 * 		Site event
	 */
	protected void handleSiteEvent(Context context, SiteEvent event) {
		TreeSet<Arc> beachLine = context.beachLine;

		// first site event
		if (beachLine.size() == 0) {
			beachLine.add(new Arc(null, null, event.site));
			return;
		}

		// edge case which will result in a bug
		if (beachLine.size() == 1 && abs(beachLine.first().site.y - event.site.y) < eps) {
			Arc first = beachLine.first();
			BreakPoint breakPoint = new BreakPoint(context);
			Arc newArc;
			if (first.site.x < event.site.x) {
				newArc = new Arc(breakPoint, null, event.site);
				breakPoint.setLeft(first);
				breakPoint.setRight(newArc);
				first.setRight(breakPoint);
			} else {
				newArc = new Arc(null, breakPoint, event.site);
				breakPoint.setLeft(newArc);
				breakPoint.setRight(first);
				first.setLeft(breakPoint);
			}
			context.breakPoints.add(breakPoint);
			beachLine.add(newArc);

			// set edge
			Point point1 = breakPoint.getPoint();
			context.voronoiPoints.add(point1);
			VoronoiBox.Edge edge = new VoronoiBox.Edge(context.cells.get(first.site),
					context.cells.get(event.site));
			edge.setPoint1(point1);
			breakPoint.setEdge(edge, false);
			context.edges.add(edge);

			return;
		}

		// find arc above site
		Arc above = beachLine.floor(new ArcQuery(event.site.x));
		if (above.circleEvent != null) {   // false alarm
			context.eventQueue.remove(above.circleEvent);
			above.setCircleEvent(null);
		}

		// insert new arc
		BreakPoint origL = above.left;
		BreakPoint origR = above.right;
		BreakPoint breakL = new BreakPoint(context);
		BreakPoint breakR = new BreakPoint(context);

		Arc newLeft = new Arc(origL, breakL, above.site);
		Arc newCenter = new Arc(breakL, breakR, event.site);
		Arc newRight = new Arc(breakR, origR, above.site);

		breakL.setLeft(newLeft);
		breakL.setRight(newCenter);
		breakR.setLeft(newCenter);
		breakR.setRight(newRight);

		if (origL != null)
			origL.setRight(newLeft);
		if (origR != null)
			origR.setLeft(newRight);

		context.breakPoints.add(breakL);
		context.breakPoints.add(breakR);

		beachLine.remove(above);
		beachLine.add(newLeft);
		beachLine.add(newCenter);
		beachLine.add(newRight);

		// set edge
		VoronoiBox.Edge edge = new VoronoiBox.Edge(context.cells.get(above.site),
				context.cells.get(event.site));
		breakL.setEdge(edge, true);
		breakR.setEdge(edge, false);
		context.edges.add(edge);

		// check for circle events
		this.checkForCircleEvent(context, newLeft);
		this.checkForCircleEvent(context, newRight);
	}

	/**
	 * Handle <i>circle event</i>
	 *
	 * @param context
	 * 		Current context
	 * @param event
	 * 		Circle event
	 */
	protected void handleCircleEvent(Context context, CircleEvent event) {
		Arc target = event.target;
		TreeSet<Arc> beachLine = context.beachLine;
		if (target.left == null || target.right == null)
			return;

		// false alarms
		BreakPoint targetL = target.left;
		if (targetL.left.circleEvent != null) {
			context.eventQueue.remove(targetL.left.circleEvent);
			targetL.left.setCircleEvent(null);
		}
		BreakPoint targetR = target.right;
		if (targetR.right.circleEvent != null) {
			context.eventQueue.remove(targetR.right.circleEvent);
			targetR.right.setCircleEvent(null);
		}

		// update beach line
		BreakPoint newBreakPoint = new BreakPoint(context, targetL.left, targetR.right);

		Point point1 = newBreakPoint.getPoint();
		context.voronoiPoints.add(point1);

		targetL.finish(point1);
		targetR.finish(point1);

		targetL.left.setRight(newBreakPoint);
		targetR.right.setLeft(newBreakPoint);

		context.breakPoints.add(newBreakPoint);
		beachLine.remove(target);

		// set edge
		VoronoiBox.Edge edge = new VoronoiBox.Edge(context.cells.get(targetL.left.site),
				context.cells.get(targetR.right.site));
		edge.setPoint1(point1);
		newBreakPoint.setEdge(edge, false);
		context.edges.add(edge);

		// check for circle events
		checkForCircleEvent(context, targetL.left);
		checkForCircleEvent(context, targetR.right);
	}

	/**
	 * A circle event is generated if and only if the target arc's site, the arc left
	 * of the target arc's site and the site of the right one are counterclockwise.<br />
	 * The final <i>circle event</i>'s point must be under the current sweep line, or
	 * the target arc would not exist.
	 *
	 * @param context
	 * 		Current context
	 * @param arc
	 * 		Target arc
	 */
	protected void checkForCircleEvent(Context context, Arc arc) {
		if (arc.left == null || arc.right == null)
			return;

		final Point s1 = arc.left.left.site;
		final Point s2 = arc.site;
		final Point s3 = arc.right.right.site;

		// points must be counterclockwise
		if ((s2.x - s1.x) * (s3.y - s1.y) - (s2.y - s1.y) * (s3.x - s1.x) <= 0)
			return;

		// calculate center of the sites' circumcircle
		// x and y are solved using Cramer's Rule
		final double x1 = s1.x, x2 = s2.x, x3 = s3.x;
		final double y1 = s1.y, y2 = s2.y, y3 = s3.y;
		final double
				a = x2 - x1,
				b = y2 - y1,
				c = x3 - x2,
				d = y3 - y2,
				sqY2 = square(y2),
				sqX2 = square(x2),
				e = (sqY2 - y1 * y1 + sqX2 - x1 * x1) / 2,
				f = (y3 * y3 - sqY2 + x3 * x3 - sqX2) / 2;
		final double matD = a * d - c * b;
		if (Math.abs(matD) < eps)    // equation has no solution
			return;
		// ( x, y ) is the circle's center
		final double x = (e * d - f * b) / matD;
		final double y = (a * f - c * e) / matD;

		// position of the circle event
		final double radius = Math.sqrt(square(x - x2) + square(y - y2));
		Point eventPoint = new Point(x, y + radius);

		// add circle event to the queue
		CircleEvent event = new CircleEvent(arc, eventPoint);
		context.eventQueue.offer(event);
		arc.setCircleEvent(event);
	}
}
