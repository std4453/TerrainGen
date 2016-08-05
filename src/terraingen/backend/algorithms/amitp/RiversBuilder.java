package terraingen.backend.algorithms.amitp;

import terraingen.backend.nodegraph.IProcessor;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

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

		Stream.generate(() -> random.nextInt(size)).limit(this.attempts).map(corners::get)
				.parallel().forEach(corner -> {
			Map.Edge downslope = MapData.DataDownslope.get(corner);
			while (downslope != null &&
					MapData.DataRiver.get(downslope) != MapData.DataRiver.RIVER) {
				MapData.DataRiver.set(downslope, MapData.DataRiver.RIVER);
				corner = downslope.otherCorner(corner);
				downslope = MapData.DataDownslope.get(corner);
			}
		});

		return input;
	}
}
