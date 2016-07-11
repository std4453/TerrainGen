package terraingen.backend.commons.voronoi;

import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.Point;
import terraingen.backend.commons.PointBox;
import terraingen.backend.nodegraph.IProcessor;

import java.util.*;

/**
 * FIXME: JUST LET THE ERRORS BE THERE
 */
public class Fortune implements IProcessor<PointBox, VoronoiBox> {
	private static double square(double n) {
		return n * n;
	}

	private static double abs(double n) {
		return n > 0 ? n : -n;
	}

	protected static class Arc implements Comparable<Arc> {
		public BreakPoint left, right;
		public Point site;
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
			return this.right != null ? this.right.getPoint().x : Double
					.POSITIVE_INFINITY;
		}

		@Override
		public int compareTo(Arc o) {
			if (this == o)
				return 0;
			if (this instanceof ArcQuery && o instanceof ArcQuery)
				return Double.compare(((ArcQuery) this).query, ((ArcQuery) o).query);
			if (this instanceof ArcQuery)
				return -o.compareTo(this);
			double tl = this.getLeftX(), tr = this.getRightX(), ol = o.getLeftX(), or = o
					.getRightX();
			if (o instanceof ArcQuery && tl <= ol && tr >= or)
				return 0;
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

	protected static class BreakPoint {
		public Context context;
		public Arc left, right;
		public Point finalPoint;
		public Point beginPoint;

		public BreakPoint(Context context, Arc left, Arc right) {
			this(context);
			this.left = left;
			this.right = right;
			this.finalPoint = null;
		}

		/**
		 * In case {@link Arc} requires {@code BreakPoint} to be instantiated first.
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

		public Point getPoint() {
			final Point s1 = this.left.site;
			final Point s2 = this.right.site;
			final double x1 = s1.x, x2 = s2.x;
			final double y1 = s1.y, y2 = s2.y;
			final double y0 = this.context.sweepLine;

			double x, y;
			if (y1 == y2) {
				x = (x1 + x2) / 2;
				y = (y0 + y1) / 2 - (y0 - y1) * square(x1 - x2) / 8;
			} else {
				final double a = y1 - y2;
				final double b = 2 * ((y2 * x1 - y1 * x2) + y0 * (x2 - x1));
				final double c = (y0 - y1) * (y1 - y2) * (y2 - y0) + (square(
						x2) * y1 - square(x1) * y2) + y0 * (square(x1) - square(x2));
				final double delta = square(b) - 4 * a * c;
				double sqrtDelta = delta < 1e-3 ? 0 : Math.sqrt(delta);
				sqrtDelta /= a * 2;
				final double axis = -b / a / 2;

//				x = y1 > y2 ? (axis + sqrtDelta) : (axis - sqrtDelta);
				x = axis + sqrtDelta;
//				x = axis - sqrtDelta;
				y = (y1 + y2) / 2 + (x1 - x2) * (x1 + x2 - 2 * x) / 2 / (y1 - y2);
			}

			return new Point(x, y);
		}

		public void begin() {
			this.beginPoint = this.getPoint();
		}

		public void finish() {
			this.finalPoint = this.getPoint();
			this.context.edges.add(new VoronoiBox.Edge(this.beginPoint, this.finalPoint));
		}
	}

	protected static abstract class Event implements Comparable<Event> {
		public boolean isSiteEvent() {
			return this instanceof SiteEvent;
		}

		public boolean isCircleEvent() {
			return this instanceof CircleEvent;
		}

		public double getY() {
			return 0;
		}

		@Override
		public int compareTo(Event o) {
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
		public double getY() {
			return this.site.y;
		}

		@Override
		public Point getPoint() {
			return this.site;
		}
	}

	protected static class CircleEvent extends Event {
		public boolean removed;
		public Arc target;
		public Point position;

		public CircleEvent(Arc target, Point position) {
			this.target = target;
			this.position = position;
		}

		public void remove() {
			this.removed = true;
		}

		@Override
		public double getY() {
			return this.position.y;
		}

		@Override
		public Point getPoint() {
			return this.position;
		}
	}

	protected static class Context {
		public TreeSet<Arc> beachLine;
		public List<Point> points;
		public PriorityQueue<Event> eventQueue;
		public HashSet<BreakPoint> breakPoints;
		public double sweepLine;

		// DEBUG HACK START
		public List<List<Parabola>> cachedBeachLines;
		public List<Point> circleEventPoints;
		public List<CircleEvent> circleEvents;
		// DEBUG HACK END

		public Map<Point, VoronoiBox.Cell> cells;
		public List<Point> voronoiPoints;
		public List<VoronoiBox.Edge> edges;

		public Context(List<Point> points) {
			this.beachLine = new TreeSet<>();
			this.points = points;
			this.eventQueue = new PriorityQueue<>();
			this.breakPoints = new HashSet<>();
			this.sweepLine = 0;

			this.cells = new HashMap<>();
			this.voronoiPoints = new Vector<>();
			this.edges = new Vector<>();

			// DEBUG HACK START
			this.cachedBeachLines = new Vector<>();
			this.circleEventPoints = new Vector<>();
			this.circleEvents = new Vector<>();
			// DEBUG HACK END
		}

		public void setSweepLine(double sweepLine) {
			this.sweepLine = sweepLine;
		}

	}

	@Override
	public VoronoiBox process(PointBox input) {
		List<Point> points = input.getPoints();
		Context context = new Context(points);
		PriorityQueue<Event> eventQueue = context.eventQueue;

		// add all site events to queue
		for (Point point : points)
			eventQueue.offer(new SiteEvent(point));

		// add all cells
		for (Point point : points)
			context.cells.put(point, new VoronoiBox.Cell(point));

		// main loop
		while (!eventQueue.isEmpty()) {
			Event event = eventQueue.poll();
			Point position = event.getPoint();
			context.setSweepLine(position.y);

			if (event.isSiteEvent()) {
				this.handleSiteEvent(context, (SiteEvent) event);
				cacheBeachLine(context);
			} else {
				context.circleEventPoints.add(event.getPoint());
				this.handleCircleEvent(context, (CircleEvent) event);
//				if (!((CircleEvent) event).removed)
//					cacheBeachLine(context);
			}
		}

		// generate VoronoiBox
		Boundaries boundaries = input.getBoundaries();
		List<VoronoiBox.Cell> cells = new Vector<>();
		cells.addAll(context.cells.values());
		List<Point> voronoiPoints = new Vector<>();
		for (BreakPoint breakPoint : context.breakPoints) {
			if (breakPoint.finalPoint == null)
				breakPoint.finish();
			voronoiPoints.add(breakPoint.finalPoint);
		}
//		VoronoiBox voronoiBox = new VoronoiBox(boundaries, points, context.edges,
//		cells, voronoiPoints);

		// DEBUG HACK START
		VoronoiBox voronoiBox = new VoronoiBox(boundaries, points, context.edges, cells,
				voronoiPoints, context.cachedBeachLines, context.circleEventPoints,
				context.circleEvents);
		// DEBUG HACK END

		return voronoiBox;
	}

	protected void handleSiteEvent(Context context, SiteEvent event) {
		System.out.println("Site event: " + event.site.x + "," + event.site.y);
		System.out.println("Beach line:");
		for (Arc arc : context.beachLine)
			System.out.println(
					"\t" + arc.getLeftX() + " ~ " + arc.getRightX() + "  site: " +
							"" + arc.site.x + "," + arc.site.y);

		TreeSet<Arc> beachLine = context.beachLine;

		// first site event
		if (beachLine.size() == 0) {
			beachLine.add(new Arc(null, null, event.site));
			return;
		}

		// find arc above site
		Arc above = beachLine.floor(new ArcQuery(event.site.x));
		if (above.circleEvent != null) {   // false alarm
			above.circleEvent.remove();
			above.setCircleEvent(null);
		}
		System.out.println(String.format("- Above: %f ~ %f %f,%f", above.getLeftX(),
				above.getRightX(), above.site.x, above.site.y));


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
		breakL.begin();
		breakR.setLeft(newCenter);
		breakR.setRight(newRight);
		breakR.begin();
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

		// new edge

		// check for new circle events
		this.checkForCircleEvent(context, newLeft);
		this.checkForCircleEvent(context, newRight);
	}

	protected void handleCircleEvent(Context context, CircleEvent event) {
		// false alarm
		if (event.removed)
			return;

		System.out.println(String.format("Circle Event: %f ~ %f %f,%f", event.target
				.getLeftX(), event.target.getRightX(), event.target.site.x, event.target
				.site.y));

		Arc target = event.target;
		TreeSet<Arc> beachLine = context.beachLine;

		// remove arc
		System.out.println(beachLine.remove(target));

		// false alarms
		BreakPoint targetL = target.left;
		BreakPoint targetR = target.right;
		if (targetL != null && targetL.left.circleEvent != null) {
			targetL.left.circleEvent.remove();
			targetL.left.setCircleEvent(null);
		}
		if (targetR != null && targetR.right.circleEvent != null) {
			targetR.right.circleEvent.remove();
			targetR.right.setCircleEvent(null);
		}

		// update voronoi result

		// update beach line
		BreakPoint newBreakPoint = new BreakPoint(context, targetL.left, targetR.right);
		targetL.left.setRight(newBreakPoint);
		targetR.right.setLeft(newBreakPoint);
		context.breakPoints.add(newBreakPoint);
		newBreakPoint.begin();
		targetL.finish();
		targetR.finish();

		// check for circle events
		checkForCircleEvent(context, targetL.left);
		checkForCircleEvent(context, targetR.right);
	}

	protected void checkForCircleEvent(Context context, Arc arc) {
		if (arc.left == null || arc.right == null)
			return;

		// local variables decorated with final to improve performance
		// the following code solves the center of the three sites' circumcircle
		final Point s1 = arc.left.left.site;
		final Point s2 = arc.site;
		final Point s3 = arc.right.right.site;
		if ((s2.x - s1.x) * (s3.y - s1.y) - (s2.y - s1.y) * (s3.x - s1.x) <= 0)
			return;
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
			return;
		final double x = (e * d - f * b) / matD;
		final double y = (a * f - c * e) / matD;
		// ( x , y ) is the circle center

		// calculate the position of the circle event
		final double dist = Math.sqrt(square(x - x2) + square(y - y2));
//		System.out.println("Possible circle event: " + x + "," + y + dist);
		Point eventPoint = new Point(x, y + dist);
//		if (eventPoint.y < context.sweepLine)
//			return;

		// add circle event
		CircleEvent event = new CircleEvent(arc, eventPoint);
		context.eventQueue.offer(event);
		arc.setCircleEvent(event);

		// DEBUG HACK START
		CircleEvent event2 = new CircleEvent(arc, new Point(x, y));
		context.circleEvents.add(event2);
		System.out.println(
				String.format("%f, %f | %f, %f | %f, %f", x1, y1, x2, y2, x3, y3));
		// DEBUG HACK END
	}

	private void cacheBeachLine(Context context) {
		List<Parabola> parabolas = new Vector<>();
		for (Arc arc : context.beachLine)
			parabolas.add(new Parabola(arc.site, context.sweepLine, arc.getLeftX(), arc
					.getRightX()));
		context.cachedBeachLines.add(parabolas);
	}

	public static class Parabola {
		public Point focus;
		public double directrix;
		public double left, right;

		public Parabola(Point focus, double directrix, double left, double right) {
			this.focus = focus;
			this.directrix = directrix;
			this.left = left;
			this.right = right;
		}
	}
}
