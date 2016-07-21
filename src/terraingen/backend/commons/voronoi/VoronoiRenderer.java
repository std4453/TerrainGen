package terraingen.backend.commons.voronoi;

import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.Point;
import terraingen.backend.nodegraph.IProcessor;
import terraingen.utils.MathUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.Set;

import static terraingen.utils.MathUtils.round;

/**
 *
 */
public class VoronoiRenderer implements IProcessor<VoronoiBox, BufferedImage> {
	/**
	 * Configuration of rendering the {@link VoronoiBox}.
	 */
	public static class Conf {
		/**
		 * It is initialized after all the default values are initialized, or it will
		 * result in {@code null} for all {@link Color} fields.
		 */
		public static Conf defaultConf;
		public static Conf withoutCellFilling;

		private static final boolean DEFAULT_ANTIALIAS_ENABLED = true;
		private static final Color DEFAULT_BACKGROUND_COLOR = Color.WHITE;

		private static final Color DEFAULT_EDGES_COLOR = Color.BLACK;
		private static final Color DEFAULT_SITES_COLOR = Color.RED;
		private static final int DEFAULT_SITES_SIZE = 5;
		private static final Color DEFAULT_VORONOI_POINTS_COLOR = Color.BLUE;
		private static final int DEFAULT_VORONOI_POINTS_SIZE = 3;

		private static final boolean DEFAULT_CELL_FILLING_RENDERED = true;
		private static final boolean DEFAULT_EDGES_RENDERED = true;
		private static final boolean DEFAULT_SITES_RENDERED = true;
		private static final boolean DEFAULT_SITES_SIZE_RENDERED = true;
		private static final boolean DEFAULT_VORONOI_POINTS_RENDERED = true;

		static {
			defaultConf = new Conf();
			withoutCellFilling = new Conf(false, true, true, true);
		}

		public boolean antialias;
		public Color background;

		public boolean cellFilling;
		public boolean edges;
		public Color edgesColor;
		public boolean sites;
		public Color sitesColor;
		public int sitesSize;
		public boolean voronoiPoints;
		public Color voronoiPointsColor;
		public int voronoiPointsSize;

		public Conf(boolean antialias, Color background, boolean cellFilling,
					boolean edges,
					Color edgesColor, boolean sites, Color sitesColor, int sitesSize,
					boolean voronoiPoints, Color voronoiPointsColor,
					int voronoiPointsSize) {
			this.antialias = antialias;
			this.background = background;
			this.cellFilling = cellFilling;
			this.edges = edges;
			this.edgesColor = edgesColor;
			this.sites = sites;
			this.sitesColor = sitesColor;
			this.sitesSize = sitesSize;
			this.voronoiPoints = voronoiPoints;
			this.voronoiPointsColor = voronoiPointsColor;
			this.voronoiPointsSize = voronoiPointsSize;
		}

		/**
		 * Use default values for colors and point sizes
		 *
		 * @param antialias
		 * 		Whether antialias is enabled
		 * @param cellFilling
		 * 		Whether cells are filled
		 * @param edges
		 * 		Whether edges are rendered
		 * @param sites
		 * 		Whether sites are rendered
		 * @param voronoiPoints
		 * 		Whether voronoi points are rendered
		 */
		public Conf(boolean antialias, boolean cellFilling, boolean edges, boolean sites,
					boolean voronoiPoints) {
			this(antialias,
					DEFAULT_BACKGROUND_COLOR,
					cellFilling,
					edges,
					DEFAULT_EDGES_COLOR,
					sites,
					DEFAULT_SITES_COLOR,
					DEFAULT_SITES_SIZE,
					voronoiPoints,
					DEFAULT_VORONOI_POINTS_COLOR,
					DEFAULT_VORONOI_POINTS_SIZE);
		}

		public Conf(boolean cellFilling, boolean edges, boolean sites,
					boolean voronoiPoints) {
			this(DEFAULT_ANTIALIAS_ENABLED, cellFilling, edges, sites, voronoiPoints);
		}

		/**
		 * Use all default values
		 */
		public Conf() {
			this(DEFAULT_CELL_FILLING_RENDERED,
					DEFAULT_EDGES_RENDERED,
					DEFAULT_SITES_RENDERED,
					DEFAULT_VORONOI_POINTS_RENDERED);
		}

		public void setAntialias(boolean antialias) {
			this.antialias = antialias;
		}

		public void setBackground(Color background) {
			this.background = background;
		}

		public void setCellFilling(boolean cellFilling) {
			this.cellFilling = cellFilling;
		}

		public void setEdges(boolean edges) {
			this.edges = edges;
		}

		public void setEdgesColor(Color edgesColor) {
			this.edgesColor = edgesColor;
		}

		public void setEdges(boolean edges, Color edgesColor) {
			setEdges(edges);
			setEdgesColor(edgesColor);
		}

		public void setSites(boolean sites) {
			this.sites = sites;
		}

		public void setSitesColor(Color sitesColor) {
			this.sitesColor = sitesColor;
		}

		public void setSites(boolean sites, Color sitesColor, int sitesSize) {
			setSites(sites);
			setSitesColor(sitesColor);
			setSitesSize(sitesSize);
		}

		public void setSitesSize(int sitesSize) {
			this.sitesSize = sitesSize;
		}

		public void setVoronoiPoints(boolean voronoiPoints) {
			this.voronoiPoints = voronoiPoints;
		}

		public void setVoronoiPointsColor(Color voronoiPointsColor) {
			this.voronoiPointsColor = voronoiPointsColor;
		}

		public void setVoronoiPointsSize(int voronoiPointsSize) {
			this.voronoiPointsSize = voronoiPointsSize;
		}

		public void setVoronoiPoints(boolean voronoiPoints, Color voronoiPointsColor, int
				voronoiPointsSize) {
			setVoronoiPoints(voronoiPoints);
			setVoronoiPointsColor(voronoiPointsColor);
			setVoronoiPointsSize(voronoiPointsSize);
		}
	}

	private static final int DEFAULT_WIDTH = 600;
	private static final int DEFAULT_HEIGHT = 600;

	protected Conf conf;
	protected int width, height;

	public VoronoiRenderer(Conf conf, int width, int height) {
		this.conf = conf;
		this.width = width;
		this.height = height;
	}

	public VoronoiRenderer(Conf conf) {
		this(conf, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public VoronoiRenderer() {
		this(Conf.defaultConf);
	}

	protected static Point transfer(Boundaries boundaries, Point input, BufferedImage
			image) {
		Boundaries imageBoundaries = new Boundaries(0, image.getHeight(), 0,
				image.getWidth());
		return MathUtils.transform(boundaries, input, imageBoundaries);
	}

	@Override
	public BufferedImage process(VoronoiBox input) {
		BufferedImage image = new BufferedImage(this.width, this.height,
				BufferedImage.TYPE_INT_RGB);
		renderToImage(this.conf, input, image);
		return image;
	}

	public static void renderToImage(VoronoiBox input, BufferedImage image) {
		renderToImage(Conf.defaultConf, input, image);
	}

	public static void renderToImage(Conf conf, VoronoiBox input,
									 BufferedImage image) {
		Graphics2D g = image.createGraphics();
		if (conf.antialias)
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

		Boundaries boundaries = input.getBoundaries();
		int width = image.getWidth();
		int height = image.getHeight();

		// clear background
		g.setColor(conf.background);
		g.fillRect(0, 0, width, height);

		if (conf.cellFilling) {
			// fill in voronoi cells
			Random random = new Random(input.getCells().size());
			for (VoronoiBox.Cell cell : input.getCells()) {
				Color color = new Color(random.nextInt(0x1000000));
				color = new Color(color.getRed() / 2 + 128,
						color.getGreen() / 2 + 128,
						color.getBlue() / 2 + 128);
				g.setColor(color);

				Set<Point> vertices = cell.vertices;
				int n = vertices.size(), i = 0;
				int[] xPoints = new int[n], yPoints = new int[n];
				for (Point p : vertices) {
					Point pScreen = transfer(boundaries, p, image);
					xPoints[i] = (int) pScreen.x;
					yPoints[i] = (int) pScreen.y;
					++i;
				}
				g.fill(new Polygon(xPoints, yPoints, n));
			}
		}
		if (conf.edges) {
			// draw voronoi edges
			g.setColor(conf.edgesColor);
			for (VoronoiBox.Edge edge : input.getEdges()) {
				Point p1 = transfer(boundaries, edge.point1, image);
				Point p2 = transfer(boundaries, edge.point2, image);
				g.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
			}
		}
		if (conf.sites) {
			// draw sites
			g.setColor(conf.sitesColor);
			double sitesSize = conf.sitesSize;
			for (Point point : input.getPoints()) {
				Point p = transfer(boundaries, point, image);
				g.fillOval(round(p.x - sitesSize / 2), round(p.y - sitesSize / 2),
						(int) sitesSize, (int) sitesSize);
			}
		}
		if (conf.voronoiPoints) {
			// draw voronoi points
			g.setColor(conf.voronoiPointsColor);
			double voronoiPointsSize = conf.voronoiPointsSize;
			for (Point point : input.getVoronoiPoints()) {
				Point p = transfer(boundaries, point, image);
				g.fillOval(round(p.x - voronoiPointsSize / 2),
						round(p.y - voronoiPointsSize / 2), (int) voronoiPointsSize,
						(int) voronoiPointsSize);
			}
		}
	}
}
