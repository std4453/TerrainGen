package org.ajwerner.voronoi;

import java.util.*;

public class Voronoi {
	public static final double MIN_DRAW_DIM = -5;
	public static final double MAX_DRAW_DIM = 5;
	// Ghetto but just for drawing stuff
	private static final double MAX_DIM = 10;
	private static final double MIN_DIM = -10;
	private double sweepLoc;
	private final ArrayList<Point> sites;
	protected final ArrayList<VoronoiEdge> edgeList;
	protected HashSet<BreakPoint> breakPoints;
	private TreeMap<ArcKey, CircleEvent> arcs;
	private TreeSet<Event> events;

	public double getSweepLoc() {
		return this.sweepLoc;
	}

	public Voronoi(ArrayList<Point> sites) {
		this(sites, false);
	}

	public Voronoi(ArrayList<Point> sites, boolean animate) {
		// initialize data structures;
		this.sites = sites;
		this.edgeList = new ArrayList<>(sites.size());
		this.events = new TreeSet<>();
		this.breakPoints = new HashSet<>();
		this.arcs = new TreeMap<>();

		for (Point site : sites) {
			if ((site.x > MAX_DIM || site.x < MIN_DIM) || (site.y > MAX_DIM || site.y < MIN_DIM))
				throw new RuntimeException(String.format(
						"Invalid site in input, sites must be between %f and %f", MIN_DIM,
						MAX_DIM));
			this.events.add(new Event(site));
		}
		this.sweepLoc = MAX_DIM;
		do {
			Event cur = this.events.pollFirst();
			this.sweepLoc = cur.p.y;
			if (cur.getClass() == Event.class) {
				handleSiteEvent(cur);
			} else {
				CircleEvent ce = (CircleEvent) cur;
				handleCircleEvent(ce);
			}
		} while ((this.events.size() > 0));

		this.sweepLoc = MIN_DIM; // hack to draw negative infinite points
		for (BreakPoint bp : this.breakPoints) {
			bp.finish();
		}
	}

	private void handleSiteEvent(Event cur) {
		// Deal with first point case
		if (this.arcs.size() == 0) {
			this.arcs.put(new Arc(cur.p, this), null);
			return;
		}

		// Find the arc above the site
		Map.Entry<ArcKey, CircleEvent> arcEntryAbove = this.arcs.floorEntry(
				new ArcQuery(cur.p));
		Arc arcAbove = (Arc) arcEntryAbove.getKey();

		// Deal with the degenerate case where the first two points are at the same y value
		if (this.arcs.size() == 0 && arcAbove.site.y == cur.p.y) {
			VoronoiEdge newEdge = new VoronoiEdge(arcAbove.site, cur.p);
			newEdge.p1 = new Point((cur.p.x + arcAbove.site.x) / 2,
					Double.POSITIVE_INFINITY);
			BreakPoint newBreak = new BreakPoint(arcAbove.site, cur.p, newEdge, false,
					this);
			this.breakPoints.add(newBreak);
			this.edgeList.add(newEdge);
			Arc arcLeft = new Arc(null, newBreak, this);
			Arc arcRight = new Arc(newBreak, null, this);
			this.arcs.remove(arcAbove);
			this.arcs.put(arcLeft, null);
			this.arcs.put(arcRight, null);
			return;
		}

		// Remove the circle event associated with this arc if there is one
		CircleEvent falseCE = arcEntryAbove.getValue();
		if (falseCE != null) {
			this.events.remove(falseCE);
		}

		BreakPoint breakL = arcAbove.left;
		BreakPoint breakR = arcAbove.right;
		VoronoiEdge newEdge = new VoronoiEdge(arcAbove.site, cur.p);
		this.edgeList.add(newEdge);
		BreakPoint newBreakL = new BreakPoint(arcAbove.site, cur.p, newEdge, true, this);
		BreakPoint newBreakR = new BreakPoint(cur.p, arcAbove.site, newEdge, false, this);
		this.breakPoints.add(newBreakL);
		this.breakPoints.add(newBreakR);

		Arc arcLeft = new Arc(breakL, newBreakL, this);
		Arc center = new Arc(newBreakL, newBreakR, this);
		Arc arcRight = new Arc(newBreakR, breakR, this);

		this.arcs.remove(arcAbove);
		this.arcs.put(arcLeft, null);
		this.arcs.put(center, null);
		this.arcs.put(arcRight, null);

		checkForCircleEvent(arcLeft);
		checkForCircleEvent(arcRight);
	}

	private void handleCircleEvent(CircleEvent ce) {
		Arc arcRight = (Arc) this.arcs.higherKey(ce.arc);
		Arc arcLeft = (Arc) this.arcs.lowerKey(ce.arc);
		if (arcRight != null) {
			CircleEvent falseCe = this.arcs.get(arcRight);
			if (falseCe != null) this.events.remove(falseCe);
			this.arcs.put(arcRight, null);
		}
		if (arcLeft != null) {
			CircleEvent falseCe = this.arcs.get(arcLeft);
			if (falseCe != null) this.events.remove(falseCe);
			this.arcs.put(arcLeft, null);
		}
		this.arcs.remove(ce.arc);

		ce.arc.left.finish(ce.vert);
		ce.arc.right.finish(ce.vert);

		this.breakPoints.remove(ce.arc.left);
		this.breakPoints.remove(ce.arc.right);

		VoronoiEdge e = new VoronoiEdge(ce.arc.left.s1, ce.arc.right.s2);
		this.edgeList.add(e);

		// Here we're trying to figure out if the org.ajwerner.voronoi.Voronoi vertex we've found is the left
		// or right point of the new edge.
		// If the edges being traces out by these two arcs take a right turn then we know
		// that the vertex is going to be above the current point
		boolean turnsLeft = Point.ccw(arcLeft.right.edgeBegin, ce.p,
				arcRight.left.edgeBegin) == 1;
		// So if it turns left, we know the next vertex will be below this vertex
		// so if it's below and the slow is negative then this vertex is the left point
		boolean isLeftPoint = (turnsLeft) ? (e.m < 0) : (e.m > 0);
		if (isLeftPoint) {
			e.p1 = ce.vert;
		} else {
			e.p2 = ce.vert;
		}
		BreakPoint newBP = new BreakPoint(ce.arc.left.s1, ce.arc.right.s2, e,
				!isLeftPoint, this);
		this.breakPoints.add(newBP);

		arcRight.left = newBP;
		arcLeft.right = newBP;

		checkForCircleEvent(arcLeft);
		checkForCircleEvent(arcRight);
	}

	private void checkForCircleEvent(Arc a) {
		Point circleCenter = a.checkCircle();
		if (circleCenter != null) {
			double radius = a.site.distanceTo(circleCenter);
			Point circleEventPoint = new Point(circleCenter.x, circleCenter.y - radius);
			CircleEvent ce = new CircleEvent(a, circleEventPoint, circleCenter);
			this.arcs.put(a, ce);
			this.events.add(ce);
		}
	}
}

