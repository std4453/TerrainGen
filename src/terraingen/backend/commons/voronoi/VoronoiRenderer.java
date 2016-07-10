package terraingen.backend.commons.voronoi;

import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.Point;
import terraingen.backend.nodegraph.IProcessor;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 *
 */
public class VoronoiRenderer implements IProcessor<VoronoiBox, BufferedImage> {
	private static final int WIDTH = 600;
	private static final int HEIGHT = 600;
	private static final int POINT_SIZE = 5;
	private static final int VORONOI_POINT_SIZE = 3;

	protected Point transfer(Boundaries boundaries, Point input) {
		return new Point((input.x - boundaries.left) / (boundaries.right - boundaries
				.left) * WIDTH,
				(input.y - boundaries.top) / (boundaries.bottom - boundaries.top) * HEIGHT);
	}

	@Override
	public BufferedImage process(VoronoiBox input) {
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();

		Boundaries boundaries = input.getBoundaries();

		// DEBUG HACK START
		boundaries = new Boundaries(boundaries.top - 10, boundaries.bottom + 10,
				boundaries.left - 10, boundaries.right + 10);
		// DEBUG HACK END

		// clear background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		// draw voronoi edges
		g.setColor(Color.BLACK);
		for (VoronoiBox.Edge edge : input.getEdges()) {
			Point p1 = this.transfer(boundaries, edge.point1);
			Point p2 = this.transfer(boundaries, edge.point2);
			g.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
		}
		// draw points
		g.setColor(Color.RED);
		for (Point point : input.getPoints()) {
			Point p = transfer(boundaries, point);
			g.fillOval((int) p.x, (int) p.y, POINT_SIZE, POINT_SIZE);
		}
		// draw voronoi points
		g.setColor(Color.BLUE);
		for (Point point : input.getVoronoiPoints()) {
			Point p = transfer(boundaries, point);
			g.fillOval((int) p.x, (int) p.y, VORONOI_POINT_SIZE, VORONOI_POINT_SIZE);
		}

//		// DEBUG HACK START
//		// draw parabolas
//		if (input.beachLines == null)
//			return image;
//		Random random = new Random();
//		for (java.util.List<Fortune.Parabola> beachLine : input.beachLines) {
//			g.setColor(
//					new Color(random.nextInt(256), random.nextInt(256),
//							random.nextInt(256)));
//			for (Fortune.Parabola parabola : beachLine) {
//				double left = parabola.left, right = parabola.right;
//				if (left > right) {
//					double tmp = left;
//					left = right;
//					right = tmp;
//				}
//				if (left == Double.NEGATIVE_INFINITY)
//					left = -30;
//				if (right == Double.POSITIVE_INFINITY)
//					right = 30;
//				double step = (right - left) / 1000;
//				double i = left;
//				Point lastPoint = null;
//				for (; i < right; i += step) {
//					Point thisPoint = new Point(i, (parabola.directrix + parabola.focus.y)
//							/ 2 - Math.pow(i - parabola.focus.x,
//							2) / 2 / (parabola.directrix - parabola.focus.y));
//					if (lastPoint != null) {
//						Point p1 = transfer(boundaries, lastPoint);
//						Point p2 = transfer(boundaries, thisPoint);
//						g.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
//					}
//					lastPoint = thisPoint;
//				}
//			}
//		}
//		// draw circle event points
//		g.setColor(Color.MAGENTA);
//		for (Point point : input.circleEvents) {
//			Point p = transfer(boundaries, point);
//			g.fillOval((int) p.x, (int) p.y, 5, 5);
//		}
//		// DEBUG HACK END

		return image;
	}
}
