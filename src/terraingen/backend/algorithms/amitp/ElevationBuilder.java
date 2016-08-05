package terraingen.backend.algorithms.amitp;

import terraingen.backend.commons.BFSQueue;
import terraingen.backend.nodegraph.IProcessor;
import terraingen.utils.Pair;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

		input.getCorners().parallelStream().forEach(
				corner -> MapData.DataElevation.set(corner, Double.POSITIVE_INFINITY));
		input.getCorners().parallelStream()
				.filter(corner -> MapData.DataIsland.get(corner) ==
						MapData.DataIsland.COAST)
				.forEach(corner -> {
					MapData.DataElevation.set(corner, 0);
					queue.offer(corner);
				});

		while (!queue.isEmpty()) {
			Map.Corner corner = queue.poll();

			MapData.DataIsland cornerIsland = MapData.DataIsland.get(corner);
			double elevation = MapData.DataElevation.get(corner);

			Stream.of(corner.e1.otherCorner(corner),
					corner.e2.otherCorner(corner),
					corner.e3.otherCorner(corner))
					.filter(corner2 -> MapData.DataIsland.get
							(corner2) != MapData.DataIsland.OCEAN)
					.map(corner2 -> new Pair<>(corner2, elevation + (
							cornerIsland == MapData.DataIsland.LAKE ||
									MapData.DataIsland.get(corner2) ==
											MapData.DataIsland.LAKE ? 0 : 1)))
					.filter(pair -> (pair.b < MapData.DataElevation.get(pair.a)))
					.forEach(pair -> {
						MapData.DataElevation.set(pair.a, pair.b);
						queue.offer(pair.a);
					});
		}

		// redistribute elevations
		List<Map.Corner> corners = input.getCorners().parallelStream()
				.filter(corner -> MapData
						.DataIsland.isOnLand(MapData.DataIsland.get(corner)))
				.sorted((a, b) -> Double.compare(MapData.DataElevation.get(a),
						MapData.DataElevation.get(b)))
				.collect(Collectors.toList());
		for (int i = 0, size = corners.size(); i < size; ++i)
			MapData.DataElevation.set(corners.get(i), redistribute(i / (size - 1d)));

		// calc cell elevation
		input.getCenters().parallelStream().forEach(center -> MapData.DataElevation.set
				(center, center.corners.stream().collect(
						Collectors.averagingDouble(MapData.DataElevation::get))));

		// calculate downslopes
		input.getCorners().parallelStream()
				.filter(corner -> MapData.DataIsland.get(corner) ==
						MapData.DataIsland.LAND)
				.forEach(corner -> MapData.DataDownslope.set(corner,
						Stream.of(corner.e1, corner.e2, corner.e3)
								.map(edge -> new Pair<>(edge, MapData.DataElevation.get(
										edge.otherCorner(corner))))
								.collect(Collectors.minBy(
										(p1, p2) -> Double.compare(p1.b, p2.b))
								).orElse(null).a));

		return input;
	}
}
