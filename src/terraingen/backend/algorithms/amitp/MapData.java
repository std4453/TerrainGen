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
	 */
	public enum DataIsland {
		OCEAN, LAKE, LAND;

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
			if (dataIsland != null) center.setData(key, dataIsland);
		}

		public static void set(Map.Corner corner, DataIsland dataIsland) {
			if (dataIsland != null) corner.setData(key, dataIsland);
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
			if (dataRiver != null) edge.setData(key, dataRiver);
		}
	}

	/**
	 * Target: Center, Corner<br />
	 */
	public static class DataMoisture {
		public static final String key = "moisture";
		public static final double def = 0d;

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
}
