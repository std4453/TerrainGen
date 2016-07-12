package terraingen.backend.commons.voronoi;

import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.Point;
import terraingen.backend.nodegraph.IProcessor;

import java.awt.*;
import java.awt.image.BufferedImage;

import static terraingen.utils.MathUtils.round;

/**
 *
 */
public class VoronoiRenderer implements IProcessor<VoronoiBox, BufferedImage> {
	private static final int WIDTH = 600;
	private static final int HEIGHT = 600;
	private static final int POINT_SIZE = 6;
	private static final int VORONOI_POINT_SIZE = 3;

	protected static Point transfer(Boundaries boundaries, Point input, BufferedImage
			image) {
		double bWidth = boundaries.right - boundaries.left;
		double bHeight = boundaries.bottom - boundaries.top;
		double iWidth = image.getWidth();
		double iHeight = image.getHeight();

		return new Point((input.x - boundaries.left) / bWidth * iWidth,
				(input.y - boundaries.top) / bHeight * iHeight);
	}

	@Override
	public BufferedImage process(VoronoiBox input) {
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT,
				BufferedImage.TYPE_INT_RGB);
		renderToImage(input, image);
		return image;
	}

	public static void renderToImage(VoronoiBox input, BufferedImage image) {
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		Boundaries boundaries = input.getBoundaries();

		// clear background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		// draw voronoi edges
		g.setColor(Color.BLACK);
		for (VoronoiBox.Edge edge : input.getEdges()) {
			Point p1 = transfer(boundaries, edge.point1, image);
			Point p2 = transfer(boundaries, edge.point2, image);
			g.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
		}
		// draw points
		g.setColor(Color.RED);
		for (Point point : input.getPoints()) {
			Point p = transfer(boundaries, point, image);
			g.fillOval(round(p.x - (double) POINT_SIZE / 2),
					round(p.y - (double) POINT_SIZE / 2), POINT_SIZE, POINT_SIZE);
		}
		// draw voronoi points
		g.setColor(Color.BLUE);
		for (Point point : input.getVoronoiPoints()) {
			Point p = transfer(boundaries, point, image);
			g.fillOval(round(p.x - (double) VORONOI_POINT_SIZE / 2), round(p.y - (double)
					VORONOI_POINT_SIZE / 2), VORONOI_POINT_SIZE, VORONOI_POINT_SIZE);
		}
	}
}
