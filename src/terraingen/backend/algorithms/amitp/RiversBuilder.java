package terraingen.backend.algorithms.amitp;

import terraingen.backend.nodegraph.IProcessor;

import java.util.List;
import java.util.Random;

/**
 *
 */
public class RiversBuilder implements IProcessor<Map, Map> {
	protected long seed;
	protected int attempts;

	public RiversBuilder(long seed, int attempts) {
		this.seed = seed;
		this.attempts = attempts;
	}

	@Override
	public Map process(Map input) {
		Random random = new Random(this.seed);

		List<Map.Corner> corners = input.getCorners();
		int size = corners.size();

		for (int i = 0; i < this.attempts; ++i) {
			int index = random.nextInt(size);
			Map.Corner corner = corners.get(index);
			Map.Edge downslope = MapData.DataDownslope.get(corner);
			while (downslope != null) {
				if (MapData.DataIsland.get(corner) == MapData.DataIsland.OCEAN)
					System.out.println("asasdasdd");
				MapData.DataRiver.set(downslope, MapData.DataRiver.RIVER);
				corner = downslope.otherCorner(corner);
				downslope = MapData.DataDownslope.get(corner);
			}
		}

		return input;
	}
}
