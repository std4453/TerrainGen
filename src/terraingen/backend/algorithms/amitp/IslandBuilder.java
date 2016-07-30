package terraingen.backend.algorithms.amitp;

import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.Point;
import terraingen.backend.commons.grid.Grid;
import terraingen.backend.commons.grid.GridTo01Mapper;
import terraingen.backend.commons.noise.GridSimplex;
import terraingen.backend.nodegraph.Executor;
import terraingen.backend.nodegraph.IProcessor;
import terraingen.backend.nodegraph.SimpleSupplier;
import terraingen.backend.nodegraph.SupplierNode;
import terraingen.utils.MathUtils;

import static terraingen.backend.nodegraph.NodeGraphHelper.create;
import static terraingen.backend.nodegraph.NodeGraphHelper.embrace;
import static terraingen.utils.MathUtils.floor;
import static terraingen.utils.MathUtils.length;
import static terraingen.utils.MathUtils.square;

/**
 *
 */
public class IslandBuilder implements IProcessor<Map, Map> {
	private static final int DEFAULT_WIDTH = 4;
	private static final int DEFAULT_HEIGHT = 4;
	private static final double DEFAULT_INTERVAL = 0.01d;

	private static final Boundaries normBoundaries = new Boundaries(-1, 1, -1, 1);

	protected Grid noise;
	protected Boundaries boundaries;

	@SuppressWarnings("unchecked")
	public IslandBuilder(long seed, int width, int height, double interval) {
		Boundaries boundaries = new Boundaries(width, height);
		this.noise = Executor.execute((SupplierNode<Grid>) embrace(
				create(new SimpleSupplier<>(boundaries)),
				create(new GridSimplex(seed, interval)),
				create(new GridTo01Mapper())
		));
		this.boundaries = new Boundaries(this.noise.getWidth(), this.noise.getHeight());
	}

	public IslandBuilder(long seed, int width, int height) {
		this(seed, width, height, DEFAULT_INTERVAL);
	}

	public IslandBuilder(long seed) {
		this(seed, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	@Override
	public Map process(Map input) {
		Boundaries mapBoundaries = input.getBoundaries();
		for (Map.Corner corner : input.getCorners()) {
			MapData.DataIsland island = MapData.DataIsland.OCEAN;
			if (mapBoundaries.inBoundaries(corner.point)) {
				Point gridPoint = MathUtils.transform(mapBoundaries, corner.point,
						this.boundaries);
				double value = this.noise.get(floor(gridPoint.x), floor(gridPoint.y));
				Point normPoint = MathUtils.transform(mapBoundaries, corner.point,
						normBoundaries);
				island = value > .1 + 1d * (square(length(normPoint))) ?
						MapData.DataIsland.LAND : MapData.DataIsland.OCEAN;
			}
			MapData.DataIsland.set(corner, island);
		}

		for (Map.Center center : input.getCenters()) {
			MapData.DataIsland island = MapData.DataIsland.LAND;
			for (Map.Corner corner : center.corners)
				if (MapData.DataIsland.get(corner) == MapData.DataIsland.OCEAN) {
					island = MapData.DataIsland.OCEAN;
					break;
				}
			MapData.DataIsland.set(center, island);
		}

		return input;
	}
}
