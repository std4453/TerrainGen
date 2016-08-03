package terraingen.backend.algorithms.amitp;

import terraingen.backend.commons.BFSQueue;
import terraingen.backend.nodegraph.IProcessor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Determines the elevation of each cell & corner according to the coastline.
 */
public class ElevationBuilder implements IProcessor<Map, Map> {
	/**
	 * Redistributes the relative elevation to the normalized final elevation.<br />
	 * The article uses {@code 1 - sqrt(1 - y)} for redistribution, whereas I choose to
	 * use {@code pow(y, 2,5)}.
	 *
	 * @param y
	 * 		Relative elevation: 0 ~ 1 for lowest ~ highest
	 */
	private double redistribute(double y) {
		return Math.pow(y, 2.5);
	}

	@Override
	public Map process(Map input) {
		BFSQueue<Map.Corner> queue = new BFSQueue<>();

		for (Map.Corner corner : input.getCorners())
			if (MapData.DataIsland.get(corner) == MapData.DataIsland.COAST) {
				MapData.DataElevation.set(corner, 0);
				queue.offer(corner);
			} else
				MapData.DataElevation.set(corner, Double.POSITIVE_INFINITY);

		while (!queue.isEmpty()) {
			Map.Corner corner = queue.poll();

			// elevations will be redistributed, so just incrementing here by 1
			MapData.DataIsland cornerIsland = MapData.DataIsland.get(corner);
			double elevation = MapData.DataElevation.get(corner);
			for (Map.Corner corner2 : new Map.Corner[]{
					corner.e1.otherCorner(corner),
					corner.e2.otherCorner(corner),
					corner.e3.otherCorner(corner),}) {
				MapData.DataIsland corner2Island = MapData.DataIsland.get(corner2);
				double newElevation = elevation + 1;
				if (corner2Island == MapData.DataIsland.OCEAN)
					continue;
				if (cornerIsland == MapData.DataIsland.LAKE ||
						corner2Island == MapData.DataIsland.LAKE)
					newElevation = elevation;
				if (newElevation < MapData.DataElevation.get(corner2)) {
					MapData.DataElevation.set(corner2, newElevation);
					queue.offer(corner2);
				}
			}
		}

		// redistribute elevations
		List<Map.Corner> corners = input.getCorners().stream()
				.filter((corner) -> MapData
						.DataIsland.isOnLand(MapData.DataIsland.get(corner)))
				.sorted((a, b) -> Double.compare(MapData.DataElevation.get(a),
						MapData.DataElevation.get(b)))
				.collect(Collectors.toList());
		for (int i = 0, size = corners.size(); i < size; ++i) {
			double newElevation = redistribute(i / (size - 1d));
			MapData.DataElevation.set(corners.get(i), newElevation);
		}

		// calc cell elevation
		for (Map.Center center : input.getCenters()) {
			double sum = 0;
			for (Map.Corner corner : center.corners)
				sum += MapData.DataElevation.get(corner);
			MapData.DataElevation.set(center, sum / center.corners.size());
		}

		return input;
	}
}
