package terraingen.backend.algorithms.amitp;

import terraingen.backend.commons.BFSQueue;
import terraingen.backend.nodegraph.IProcessor;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class MoistureBuilder implements IProcessor<Map, Map> {
	@Override
	public Map process(Map input) {
		BFSQueue<Map.Corner> queue = new BFSQueue<>();

		for (Map.Corner corner : input.getCorners()) {
			// river bank / coast ( lake & ocean aren't included for performance )
			MapData.DataIsland island = MapData.DataIsland.get(corner);
			Map.Edge downslope = MapData.DataDownslope.get(corner);
			boolean isRiver = downslope != null &&
					MapData.DataRiver.get(downslope) == MapData.DataRiver.RIVER;
			if (island == MapData.DataIsland.OCEAN ||
					island == MapData.DataIsland.LAKE ||
					island == MapData.DataIsland.COAST ||
					isRiver)
				MapData.DataMoisture.set(corner, 0);
			if (island == MapData.DataIsland.COAST || isRiver)
				queue.offer(corner);
		}

		while (!queue.isEmpty()) {
			Map.Corner corner = queue.poll();
			double moisture = MapData.DataMoisture.get(corner);
			double newMoisture = moisture + 1;
			for (Map.Corner corner2 : new Map.Corner[]{
					corner.e1.otherCorner(corner),
					corner.e2.otherCorner(corner),
					corner.e3.otherCorner(corner)}) {
				if (MapData.DataMoisture.get(corner2) > newMoisture) {
					MapData.DataMoisture.set(corner2, newMoisture);
					queue.offer(corner2);
				}
			}
		}

		for (Map.Center center : input.getCenters()) {
			double s = 0;
			for (Map.Corner corner : center.corners)
				s += MapData.DataMoisture.get(corner);
			MapData.DataMoisture.set(center, s / center.corners.size());
		}

		// redistribute moisture
		List<Map.Center> centers = input.getCenters().stream()
				.filter((center) -> MapData.DataIsland.isLand(
						MapData.DataIsland.get(center)))
				.sorted((a, b) -> Double.compare(MapData.DataMoisture.get(a),
						MapData.DataMoisture.get(b)))
				.collect(Collectors.toList());
		for (int i = 0, size = centers.size(); i < size; ++i) {
			double newMoisture = redistribute(i / (size - 1d));
			MapData.DataMoisture.set(centers.get(i), newMoisture);
		}

		return input;
	}

	private double redistribute(double y) {
		return 1 - Math.pow(y, 2.5);
	}
}
