package terraingen.backend.algorithms.amitp;

import terraingen.backend.nodegraph.IProcessor;

/**
 *
 */
public class BiomesBuilder implements IProcessor<Map, Map> {
	@Override
	public Map process(Map input) {
		input.getCenters().parallelStream().forEach(center ->
				MapData.DataBiome.set(center, getBiome(center)));

		return input;
	}

	protected MapData.DataBiome getBiome(Map.Center cell) {
		MapData.DataIsland island = MapData.DataIsland.get(cell);
		double elevation = MapData.DataElevation.get(cell);
		double moisture = MapData.DataMoisture.get(cell);

		if (island == MapData.DataIsland.OCEAN) {
			return MapData.DataBiome.OCEAN;
		} else if (island == MapData.DataIsland.LAKE) {
			return MapData.DataBiome.LAKE;
		} else if (isCellCoast(cell)) {
			return MapData.DataBiome.BEACH;
		} else if (elevation > 0.8) {
			if (moisture > 0.50) return MapData.DataBiome.SNOW;
			else if (moisture > 0.33) return MapData.DataBiome.TUNDRA;
			else if (moisture > 0.16) return MapData.DataBiome.BARE;
			else return MapData.DataBiome.SCORCHED;
		} else if (elevation > 0.6) {
			if (moisture > 0.66) return MapData.DataBiome.TAIGA;
			else if (moisture > 0.33) return MapData.DataBiome.SHRUBLAND;
			else return MapData.DataBiome.TEMPERATE_DESERT;
		} else if (elevation > 0.3) {
			if (moisture > 0.83) return MapData.DataBiome.TEMPERATE_RAIN_FOREST;
			else if (moisture > 0.50)
				return MapData.DataBiome.TEMPERATE_DECIDUOUS_DESERT;
			else if (moisture > 0.16) return MapData.DataBiome.GRASSLAND;
			else return MapData.DataBiome.TEMPERATE_DESERT;
		} else {
			if (moisture > 0.66) return MapData.DataBiome.TROPICAL_RAIN_FOREST;
			else if (moisture > 0.33)
				return MapData.DataBiome.TROPICAL_SEASONAL_FOREST;
			else if (moisture > 0.16) return MapData.DataBiome.GRASSLAND;
			else return MapData.DataBiome.SUBTROPICAL_DESERT;
		}
	}

	protected boolean isCellCoast(Map.Center cell) {
		for (Map.Corner corner : cell.corners)
			if (MapData.DataIsland.get(corner) == MapData.DataIsland.COAST)
				return true;
		return false;
	}
}
