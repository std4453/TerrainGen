package terraingen.backend.algorithms.amitp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import terraingen.backend.commons.Boundaries;
import terraingen.backend.nodegraph.IProcessor;
import terraingen.utils.MathUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;

/**
 * Render different <b>types</b> of {@link Map}, all in one.<br /><br />
 * {@code MapRenderer} takes a {@link Type} as description of the type of {@link Map}
 * that is rendered. Available types are:
 * <ol>
 * <li>"island": represent water and land cells by different colors.</li>
 * <li>"elevation": render elevation of land cells. ( and water cells with a
 * invariable bluish color )</li>
 * <li>"rivers": render rivers along edges. ( it will not fill in cells so that can
 * be rendered after filling the cells )</li>
 * <li>"moisture": render moisture level.</li>
 * <li>"biomes": render biomes. ( according to the post of the algorithm )</li>
 * </ol>
 * Note that {@code MapRenderer} renders <b>ONLY</b> regular straight edges of the
 * {@link Map} ( or, to say, the
 * {@linkplain terraingen.backend.commons.voronoi.VoronoiBox VoronoiBox} ). No
 * blending, no noisy edges, no color transitions.
 */
public class MapRenderer implements IProcessor<Map, BufferedImage> {
	private static final Log log = LogFactory.getLog(MapRenderer.class);

	public enum Type {
		ISLAND {
			@Override
			public void execute(Map map, BufferedImage image) {
				renderToImageIsland(map, image);
			}
		}, ELEVATION {
			@Override
			public void execute(Map map, BufferedImage image) {
				renderToImageElevation(map, image);
			}
		}, RIVERS {
			@Override
			public void execute(Map map, BufferedImage image) {
				renderToImageRivers(map, image);
			}
		}, MOISTURE {
			@Override
			public void execute(Map map, BufferedImage image) {
				renderToImageMoisture(map, image);
			}
		}, BIOMES {
			@Override
			public void execute(Map map, BufferedImage image) {
				renderToImageBiomes(map, image);
			}
		};

		public abstract void execute(Map map, BufferedImage image);
	}

	private static final int DEFAULT_WIDTH = 600;
	private static final int DEFAULT_HEIGHT = 600;

	protected Type type;
	protected int width, height;

	public MapRenderer(Type type, int width, int height) {
		this.type = type;
		this.width = width;
		this.height = height;
	}

	public MapRenderer(Type type) {
		this(type, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	@Override
	public BufferedImage process(Map input) {
		BufferedImage image = new BufferedImage(this.width, this.height,
				BufferedImage.TYPE_INT_ARGB);
		renderToImage(this.type, input, image);
		return image;
	}

	public static void renderToImage(Type type, Map map, BufferedImage image) {
		type.execute(map, image);
	}

	public static void renderToImageIsland(Map map, BufferedImage image) {
		renderToImageWithShader(map, image, IShader.island);
	}

	public static void renderToImageElevation(Map map, BufferedImage image) {
		renderToImageWithShader(map, image, IShader.elevation);
	}

	public static void renderToImageRivers(Map map, BufferedImage image) {
		renderToImageWithShader(map, image, IShader.rivers);
	}

	public static void renderToImageMoisture(Map map, BufferedImage image) {
		renderToImageWithShader(map, image, IShader.moisture);
	}

	public static void renderToImageBiomes(Map map, BufferedImage image) {
		renderToImageWithShader(map, image, IShader.biomes);
	}

	private interface ICellShader {
		ICellShader transparent = new TransparentCellShader();
		ICellShader island = new IslandCellShader();
		ICellShader elevation = new ElevationCellShader();
		ICellShader moisture = new MoistureCellShader();
		ICellShader biomes = new BiomesCellShader();

		Color colorizeCell(Map map, Map.Center cell);
	}

	private static class TransparentCellShader implements ICellShader {
		@Override
		public Color colorizeCell(Map map, Map.Center cell) {
			return new Color(0, 0, 0, 0);
		}
	}

	private static class IslandCellShader implements ICellShader {
		private static final Color COLOR_OCEAN = new Color(54, 54, 97);
		private static final Color COLOR_LAKE = new Color(91, 132, 173);
		private static final Color COLOR_LAND = new Color(179, 166, 146);

		@Override
		public Color colorizeCell(Map map, Map.Center cell) {
			switch (MapData.DataIsland.get(cell)) {
				case OCEAN:
					return COLOR_OCEAN;
				case LAKE:
					return COLOR_LAKE;
				case LAND:
					return COLOR_LAND;
			}
			return null;    // should not happen
		}
	}

	private static class ElevationCellShader implements ICellShader {
		private static final Color COLOR_OCEAN = new Color(54, 54, 97);
		private static final Color COLOR_LOWEST = new Color(103, 148, 90);
		private static final Color COLOR_HIGHEST = new Color(251, 252, 251);

		@Override
		public Color colorizeCell(Map map, Map.Center cell) {
			if (MapData.DataIsland.get(cell) == MapData.DataIsland.OCEAN)
				return COLOR_OCEAN;

			double elevation = MapData.DataElevation.get(cell);
			double invElevation = 1 - elevation;
			int r1 = COLOR_LOWEST.getRed(),
					r2 = COLOR_HIGHEST.getRed(),
					g1 = COLOR_LOWEST.getGreen(),
					g2 = COLOR_HIGHEST.getGreen(),
					b1 = COLOR_LOWEST.getBlue(),
					b2 = COLOR_HIGHEST.getBlue();
			return new Color((int) (r1 * invElevation + r2 * elevation),
					(int) (g1 * invElevation + g2 * elevation),
					(int) (b1 * invElevation + b2 * elevation));
		}
	}

	private static class MoistureCellShader implements ICellShader {
		private static final Color COLOR_OCEAN = new Color(54, 54, 97);
		private static final Color COLOR_LAKE = new Color(51, 102, 153);
		private static final Color COLOR_DRY = new Color(197, 202, 151);
		private static final Color COLOR_WET = new Color(52, 105, 103);

		@Override
		public Color colorizeCell(Map map, Map.Center cell) {
			double moisture = MapData.DataMoisture.get(cell);
			double invMoisture = 1 - moisture;
			int r1 = COLOR_DRY.getRed(),
					r2 = COLOR_WET.getRed(),
					g1 = COLOR_DRY.getGreen(),
					g2 = COLOR_WET.getGreen(),
					b1 = COLOR_DRY.getBlue(),
					b2 = COLOR_WET.getBlue();
			return new Color((int) (r1 * invMoisture + r2 * moisture),
					(int) (g1 * invMoisture + g2 * moisture),
					(int) (b1 * invMoisture + b2 * moisture));
		}
	}

	private static class BiomesCellShader implements ICellShader {
		private static final Color COLOR_SNOW = new Color(248, 248, 248);
		private static final Color COLOR_TUNDRA = new Color(221, 221, 187);
		private static final Color COLOR_BARE = new Color(187, 187, 187);
		private static final Color COLOR_SCORCHED = new Color(153, 153, 153);
		private static final Color COLOR_TAIGA = new Color(204, 212, 187);
		private static final Color COLOR_SHRUBLAND = new Color(196, 204, 187);
		private static final Color COLOR_TEMPERATE_DESERT = new Color(228, 232, 202);
		private static final Color COLOR_TEMPERATE_RAIN_FOREST = new Color(164, 196, 168);
		private static final Color COLOR_TEMPERATE_DECIDUOUS_DESERT =
				new Color(180, 201, 169);
		private static final Color COLOR_GRASSLAND = new Color(196, 212, 170);
		private static final Color COLOR_TROPICAL_RAIN_FOREST = new Color(156, 187, 169);
		private static final Color COLOR_TROPICAL_SEASONAL_FOREST =
				new Color(169, 204, 164);
		private static final Color COLOR_SUBTROPICAL_DESERT = new Color(233, 221, 199);
		private static final Color COLOR_OCEAN = new Color(54, 54, 97);
		private static final Color COLOR_LAKE = new Color(85, 125, 166);
		private static final Color COLOR_BEACH = new Color(172, 159, 139);


		@Override
		public Color colorizeCell(Map map, Map.Center cell) {
			switch (MapData.DataBiome.get(cell)) {
				case SNOW:
					return COLOR_SNOW;
				case TUNDRA:
					return COLOR_TUNDRA;
				case BARE:
					return COLOR_BARE;
				case SCORCHED:
					return COLOR_SCORCHED;
				case TAIGA:
					return COLOR_TAIGA;
				case SHRUBLAND:
					return COLOR_SHRUBLAND;
				case TEMPERATE_DESERT:
					return COLOR_TEMPERATE_DESERT;
				case TEMPERATE_RAIN_FOREST:
					return COLOR_TEMPERATE_RAIN_FOREST;
				case TEMPERATE_DECIDUOUS_DESERT:
					return COLOR_TEMPERATE_DECIDUOUS_DESERT;
				case GRASSLAND:
					return COLOR_GRASSLAND;
				case TROPICAL_RAIN_FOREST:
					return COLOR_TROPICAL_RAIN_FOREST;
				case TROPICAL_SEASONAL_FOREST:
					return COLOR_TROPICAL_SEASONAL_FOREST;
				case SUBTROPICAL_DESERT:
					return COLOR_SUBTROPICAL_DESERT;
				case OCEAN:
					return COLOR_OCEAN;
				case LAKE:
					return COLOR_LAKE;
				case BEACH:
					return COLOR_BEACH;
			}
			return null;    // should not happen
		}
	}

	private interface IEdgeShader {
		IEdgeShader vanilla = new VanillaEdgeShader();
		IEdgeShader rivers = new RiversEdgeShader();

		Color colorizeEdge(Map map, Map.Edge edge);

		float getEdgeWidth(Map map, Map.Edge edge);
	}

	private static class VanillaEdgeShader implements IEdgeShader {
		@Override
		public Color colorizeEdge(Map map, Map.Edge edge) {
			return Color.black;
		}

		@Override
		public float getEdgeWidth(Map map, Map.Edge edge) {
			return 1;
		}
	}

	private static class RiversEdgeShader implements IEdgeShader {
		private static final Color COLOR_RIVER = Color.BLUE;
		private static final Color COLOR_NON_RIVER = Color.BLACK;
		private static final float EDGE_WIDTH_RIVER = 3;
		private static final float EDGE_WIDTH_NON_RIVER = 1;

		@Override

		public Color colorizeEdge(Map map, Map.Edge edge) {
			switch (MapData.DataRiver.get(edge)) {
				case RIVER:
					return COLOR_RIVER;
				case NON_RIVER:
					return COLOR_NON_RIVER;
			}
			return null;    // should not happen
		}

		@Override
		public float getEdgeWidth(Map map, Map.Edge edge) {
			switch (MapData.DataRiver.get(edge)) {
				case RIVER:
					return EDGE_WIDTH_RIVER;
				case NON_RIVER:
					return EDGE_WIDTH_NON_RIVER;
			}
			return 0f;    // should not happen
		}
	}

	private interface IShader extends ICellShader, IEdgeShader {
		IShader island = new CompositeShader(ICellShader.island, IEdgeShader.vanilla);
		IShader elevation = new CompositeShader(ICellShader.elevation,
				IEdgeShader.vanilla);
		IShader rivers = new CompositeShader(ICellShader.transparent, IEdgeShader.rivers);
		IShader moisture = new CompositeShader(ICellShader.moisture, IEdgeShader.vanilla);
		IShader biomes = new CompositeShader(ICellShader.biomes, IEdgeShader.vanilla);
	}

	private static class CompositeShader implements IShader {
		protected ICellShader cellShader;
		protected IEdgeShader edgeShader;

		public CompositeShader(
				ICellShader cellShader,
				IEdgeShader edgeShader) {
			this.cellShader = cellShader;
			this.edgeShader = edgeShader;
		}

		@Override
		public Color colorizeCell(Map map, Map.Center cell) {
			return this.cellShader.colorizeCell(map, cell);
		}

		@Override
		public Color colorizeEdge(Map map, Map.Edge edge) {
			return this.edgeShader.colorizeEdge(map, edge);
		}

		@Override
		public float getEdgeWidth(Map map, Map.Edge edge) {
			return this.edgeShader.getEdgeWidth(map, edge);
		}
	}

	private static void renderToImageWithShader(Map map, BufferedImage image,
												IShader shader) {
		if (map == null) {
			log.error("Non-null map expected");
			return;
		}
		if (image == null) {
			log.error("Non-null image expected.");
			return;
		}
		if (shader == null) {
			log.error("Null shader: why should this happen?");
			return;
		}

		int iWidth = image.getWidth();
		int iHeight = image.getHeight();
		Boundaries imageBoundaries = new Boundaries(iWidth, iHeight);
		Boundaries boundaries = map.boundaries;

		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// render cells
		for (Map.Center cell : map.getCenters()) {
			g.setColor(shader.colorizeCell(map, cell));

			Collection<Map.Corner> corners = cell.corners;
			int n = corners.size(), i = 0;
			int[] xPoints = new int[n], yPoints = new int[n];
			for (Map.Corner corner : corners) {
				terraingen.backend.commons.Point pScreen = MathUtils.transform(boundaries,
						corner.point, imageBoundaries);
				xPoints[i] = (int) pScreen.x;
				yPoints[i] = (int) pScreen.y;
				++i;
			}
			g.fill(new Polygon(xPoints, yPoints, n));
		}

		// render edges
		for (Map.Edge edge : map.getEdges()) {
			g.setColor(shader.colorizeEdge(map, edge));
			g.setStroke(new BasicStroke(shader.getEdgeWidth(map, edge)));

			terraingen.backend.commons.Point p1 = edge.c1.point, p2 = edge.c2.point;
			p1 = MathUtils.transform(boundaries, p1, imageBoundaries);
			p2 = MathUtils.transform(boundaries, p2, imageBoundaries);

			g.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
		}
	}
}
