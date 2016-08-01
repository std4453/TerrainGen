package terraingen.backend.algorithms.amitp;

import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.Point;
import terraingen.backend.commons.grid.Grid;
import terraingen.backend.commons.grid.GridCombiner;
import terraingen.backend.commons.grid.GridMapper;
import terraingen.backend.commons.grid.GridTo01Mapper;
import terraingen.backend.commons.noise.GridPerlin;
import terraingen.backend.nodegraph.*;
import terraingen.utils.MathUtils;

import java.util.Arrays;
import java.util.List;

import static terraingen.backend.nodegraph.NodeGraphHelper.create;
import static terraingen.backend.nodegraph.NodeGraphHelper.embrace;
import static terraingen.backend.nodegraph.NodeGraphHelper.embraceStatement;
import static terraingen.backend.nodegraph.NodeGraphHelper.embraceSupplier;
import static terraingen.utils.MathUtils.floor;
import static terraingen.utils.MathUtils.length;

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
		List<SupplierNode<Grid>> grids = Arrays.asList(new SupplierNode[]{
				// identity
				embraceSupplier(
						create(new SimpleSupplier<>(new Boundaries(width, height))),
						create(new GridPerlin(seed, interval)),
						create(new GridTo01Mapper())),
				// frequency * 2, amplitude * .7, seed + 1
				embraceSupplier(
						create(new SimpleSupplier<>(
								new Boundaries(width * 2, height * 2))),
						create(new GridPerlin(seed + 1, interval * 2)),
						create(new GridTo01Mapper()),
						create(new GridMapper(
								embraceStatement(create((Double n) -> n * .7))))),
				// frequency * 4, amplitude * .7, seed + 2
				embraceSupplier(
						create(new SimpleSupplier<>(
								new Boundaries(width * 4, height * 4))),
						create(new GridPerlin(seed + 2, interval * 4)),
						create(new GridTo01Mapper()),
						create(new GridMapper(
								embraceStatement(create((Double n) -> n * .7))))),
				// frequency * 8, amplitude * .4, seed + 3
				embraceSupplier(
						create(new SimpleSupplier<>(
								new Boundaries(width * 8, height * 8))),
						create(new GridPerlin(seed + 3, interval * 8)),
						create(new GridTo01Mapper()),
						create(new GridMapper(
								embraceStatement(create((Double n) -> n * .4)))))
		});
		this.noise = Executor.execute((SupplierNode<Grid>) embrace(
				create(new MultiSupplier<>(grids,
						create(new GridCombiner(), grids.size()))),
				create(new GridTo01Mapper())));
		this.boundaries = new Boundaries(this.noise.getWidth(),
				this.noise.getHeight());
	}

	public IslandBuilder(long seed, int width, int height) {
		this(seed, width, height, DEFAULT_INTERVAL);
	}

	public IslandBuilder(long seed) {
		this(seed, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	/**
	 * Calculates the see-land distribution, radially equal.<br />
	 * It currently uses tangent to generate a distribution value, the reasons are as
	 * follows:<br />
	 * <ol>
	 * <li>Tangent comes to positive and negative infinity at pi/2 and -pi/2, which
	 * almost ensures that the grid center is land and borders are ocean.</li>
	 * <li>Derivative of tangent comes fairly small at 0, which makes the
	 * distribution in the middle-part of the map ( distance from grid center about 0.5
	 * ) flat, so that the noise map will create noisy coastlines, peninsulas and
	 * islands that have plenty of details and look pretty.</li>
	 * </ol>
	 *
	 * @param distance
	 * 		Distance from map center
	 */
	private double probability(double distance) {
		return 0.35 + Math.tan(distance - 0.5);
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
				island = value > probability(length(normPoint)) ?
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
