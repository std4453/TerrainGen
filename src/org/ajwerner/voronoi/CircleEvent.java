package org.ajwerner.voronoi;

/**
 * Created by ajwerner on 12/28/13.
 */
public class CircleEvent extends Event {
	public final Arc arc;
	public final Point vert;

	public CircleEvent(Arc a, Point p, Point vert) {
		super(p);
		this.arc = a;
		this.vert = vert;
	}
}
