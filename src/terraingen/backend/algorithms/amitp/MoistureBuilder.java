package terraingen.backend.algorithms.amitp;

import terraingen.backend.commons.BFSQueue;
import terraingen.backend.nodegraph.IProcessor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public class MoistureBuilder implements IProcessor<Map, Map> {
	@Override
	public Map process(Map input) {
		BFSQueue<Map.Corner> queue = new BFSQueue<>();

		input.getCorners().parallelStream().forEach(corner -> {
			MapData.DataIsland island = MapData.DataIsland.get(corner);
			if (island == MapData.DataIsland.OCEAN || island == MapData.DataIsland.LAKE)
				MapData.DataMoisture.set(corner, 0);
		});
		input.getCorners().parallelStream().forEach(corner -> {
			// river bank / coast ( lake & ocean aren't included for performance )
			MapData.DataIsland island = MapData.DataIsland.get(corner);
			Map.Edge downslope = MapData.DataDownslope.get(corner);
			boolean isRiver = downslope != null &&
					MapData.DataRiver.get(downslope) == MapData.DataRiver.RIVER;
			if (island == MapData.DataIsland.COAST || isRiver) {
				MapData.DataMoisture.set(corner, 0);
				queue.offer(corner);
			}
		});

		while (!queue.isEmpty()) {
			Map.Corner corner = queue.poll();
			double newMoisture = MapData.DataMoisture.get(corner) + 1;
			Stream.of(corner.e1.otherCorner(corner),
					corner.e2.otherCorner(corner),
					corner.e3.otherCorner(corner))
					.filter(corner2 -> MapData.DataMoisture.get(corner2) > newMoisture)
					.forEach(corner2 -> {
						MapData.DataMoisture.set(corner2, newMoisture);
						queue.offer(corner2);
					});
		}

		input.getCenters().forEach(center -> MapData.DataMoisture.set(center,
				center.corners.parallelStream().collect(Collectors.averagingDouble(
						MapData.DataMoisture::get))));

		// redistribute moisture
		List<Map.Center> centers = input.getCenters().stream()
				.filter((center) -> MapData.DataIsland.isLand(
						MapData.DataIsland.get(center)))
				.sorted((a, b) -> Double.compare(MapData.DataMoisture.get(a),
						MapData.DataMoisture.get(b)))
				.collect(Collectors.toList());
		for (int i = 0, size = centers.size(); i < size; ++i)
			MapData.DataMoisture.set(centers.get(i), redistribute(i / (size - 1d)));

		return input;
	}

	private double redistribute(double y) {
		return 1 - Math.pow(y, 2.5);
	}
}
