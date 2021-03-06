package terraingen.backend.algorithms.amitp;

/**
 * Enums that {@code data} field of components (
 * {@linkplain terraingen.backend.algorithms.amitp.Map.Corner Corner} /
 * {@linkplain terraingen.backend.algorithms.amitp.Map.Center Center} /
 * {@linkplain terraingen.backend.algorithms.amitp.Map.Edge Edge}
 * ) in {@link Map} contain.
 */
public class MapData {
	// It can be seen that these enums / classes are designed following a simple and
	// identical pattern. As a matter of facts, some features of the pattern can be
	// ascertained:
	// 1. get() will always return a non-null value
	// 2. get() and set() cover the target classes
	//
	// And now here some thoughts I wanna write down here so that when I read my code 6
	// months later I can understand why I did this ( in case I want to implement
	// another algorithm following the same pattern ):
	// Have a look at MapRenderer.java, especially, for example, RiversEdgeShader.
	// Theoretically getting COLOR and EDGE_WIDTH from the enum DataRiver would be more
	// elegant way and easy for further development, however the reason why I separated
	// it from DataRivers is, that separating frontend ( data for rendering ) and
	// backend ( specifically whether an edge is a river ) is among the best practices.
	// Pity that creating maps is not as easy as in Javascript.

	/**
	 * Target: Center, Corner<br />
	 * Note that COAST is only assigned to corners
	 */
	public enum DataIsland {
		OCEAN, LAKE, LAND, COAST;

		public static final String key = "island";
		public static final DataIsland def = OCEAN;

		public static DataIsland get(Map.Center center) {
			DataIsland island = (DataIsland) center.getData(key);
			return island == null ? def : island;
		}

		public static DataIsland get(Map.Corner corner) {
			DataIsland island = (DataIsland) corner.getData(key);
			return island == null ? def : island;
		}

		public static void set(Map.Center center, DataIsland dataIsland) {
			center.setData(key, dataIsland);
		}

		public static void set(Map.Corner corner, DataIsland dataIsland) {
			corner.setData(key, dataIsland);
		}

		public static boolean isWater(DataIsland island) {
			return island == OCEAN || island == LAKE;
		}

		public static boolean isLand(DataIsland island) {
			return island == LAND || island == COAST;
		}

		public static boolean isOnLand(DataIsland island) {
			return island != OCEAN;
		}
	}

	/**
	 * Target: Center, Corner<br />
	 * Range: 0 ~ 1
	 */
	public static class DataElevation {
		public static final String key = "elevation";
		public static final double def = 0d;

		public static double get(Map.Center center) {
			Double elevation = (Double) center.getData(key);
			return elevation == null ? def : elevation;
		}

		public static double get(Map.Corner corner) {
			Double elevation = (Double) corner.getData(key);
			return elevation == null ? def : elevation;
		}

		public static void set(Map.Center center, double dataElevation) {
			center.setData(key, dataElevation);
		}

		public static void set(Map.Corner corner, double dataElevation) {
			corner.setData(key, dataElevation);
		}
	}

	public static class DataDownslope {
		public static final String key = "downslope";
		public static final Map.Edge def = null;

		public static Map.Edge get(Map.Corner corner) {
			Map.Edge downslope = (Map.Edge) corner.getData(key);
			return downslope == null ? def : downslope;
		}

		public static void set(Map.Corner corner, Map.Edge downslope) {
			corner.setData(key, downslope);
		}
	}

	/**
	 * Target: Edge<br />
	 */
	public enum DataRiver {
		RIVER, NON_RIVER;

		public static final String key = "river";
		public static final DataRiver def = NON_RIVER;

		public static DataRiver get(Map.Edge edge) {
			DataRiver dataRiver = (DataRiver) edge.getData(key);
			return dataRiver == null ? def : dataRiver;
		}

		public static void set(Map.Edge edge, DataRiver dataRiver) {
			edge.setData(key, dataRiver);
		}
	}

	/**
	 * Target: Center, Corner<br />
	 */
	public static class DataMoisture {
		public static final String key = "moisture";
		public static final double def = Double.POSITIVE_INFINITY;

		public static double get(Map.Center center) {
			Double moisture = (Double) center.getData(key);
			return moisture == null ? def : moisture;
		}

		public static double get(Map.Corner corner) {
			Double moisture = (Double) corner.getData(key);
			return moisture == null ? def : moisture;
		}

		public static void set(Map.Center center, double dataMoisture) {
			center.setData(key, dataMoisture);
		}

		public static void set(Map.Corner corner, double dataMoisture) {
			corner.setData(key, dataMoisture);
		}
	}

	/**
	 * Target: Center<br />
	 * Enumerated according to the picture in the article.
	 */
	public enum DataBiome {
		SNOW, TUNDRA, BARE, SCORCHED,
		TAIGA, SHRUBLAND, TEMPERATE_DESERT,
		TEMPERATE_RAIN_FOREST, TEMPERATE_DECIDUOUS_DESERT, GRASSLAND,
		TROPICAL_RAIN_FOREST, TROPICAL_SEASONAL_FOREST, SUBTROPICAL_DESERT,
		OCEAN, LAKE, BEACH;

		public static final String key = "moisture";
		public static final DataBiome def = GRASSLAND;

		public static DataBiome get(Map.Center center) {
			DataBiome biome = (DataBiome) center.getData(key);
			return biome == null ? def : biome;
		}

		public static void set(Map.Center center, DataBiome dataBiome) {
			center.setData(key, dataBiome);
		}
	}

	/**
	 * Target: Center, Corner, Edge<br />
	 * Any additional data to assign to components of the {@link Map}.
	 */
	public static class DataAny {
		public static Object get(Map.Center center, String key, Object def) {
			Object obj = center.getData(key);
			return obj == null ? def : obj;
		}

		public static Object get(Map.Center center, String key) {
			return get(center, key, null);
		}

		public static Object get(Map.Corner corner, String key, Object def) {
			Object obj = corner.getData(key);
			return obj == null ? def : obj;
		}

		public static Object get(Map.Corner corner, String key) {
			return get(corner, key, null);
		}

		public static Object get(Map.Edge edge, String key, Object def) {
			Object obj = edge.getData(key);
			return obj == null ? def : obj;
		}

		public static Object get(Map.Edge edge, String key) {
			return get(edge, key, null);
		}

		public static void set(Map.Center center, String key, Object obj) {
			center.setData(key, obj);
		}

		public static void set(Map.Corner corner, String key, Object obj) {
			corner.setData(key, obj);
		}

		public static void set(Map.Edge edge, String key, Object obj) {
			edge.setData(key, obj);
		}

		public static void remove(Map.Center center, String key) {
			center.removeData(key);
		}

		public static void remove(Map.Corner corner, String key) {
			corner.removeData(key);
		}

		public static void remove(Map.Edge edge, String key) {
			edge.removeData(key);
		}
	}

	/**
	 * Target: Center<br />
	 * Whether the cell is a border cell or not.
	 */
	public enum DataBorder {
		BORDER, CENTER;

		private static final String key = "border";
		private static final DataBorder def = CENTER;

		public static DataBorder get(Map.Center center) {
			DataBorder border = (DataBorder) center.getData(key);
			return border == null ? def : border;
		}

		public static void set(Map.Center center, DataBorder border) {
			center.setData(key, border);
		}
	}
}
