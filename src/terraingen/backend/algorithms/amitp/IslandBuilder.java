package terraingen.backend.algorithms.amitp;

import terraingen.backend.commons.BFSQueue;
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
 * Determines which corners & cells are ocean, land, coast or lake.
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

		input.getCorners().parallelStream().forEach(corner ->
				MapData.DataIsland.set(corner, MapData.DataIsland.OCEAN));
		input.getCorners().parallelStream()
				.filter(corner -> mapBoundaries.inBoundaries(corner.point))
				.forEach(corner -> {
					Point gridPoint = MathUtils.transform(mapBoundaries, corner.point,
							this.boundaries);
					double value = this.noise.get(floor(gridPoint.x), floor(gridPoint.y));
					Point normPoint = MathUtils.transform(mapBoundaries, corner.point,
							normBoundaries);
					MapData.DataIsland.set(corner,
							value > probability(length(normPoint)) ?
									MapData.DataIsland.LAND : MapData.DataIsland.OCEAN);
				});

		input.getCenters().parallelStream().forEach(center -> {
			MapData.DataIsland island = MapData.DataIsland.LAND;
			for (Map.Corner corner : center.corners)
				if (MapData.DataIsland.get(corner) == MapData.DataIsland.OCEAN) {
					island = MapData.DataIsland.OCEAN;
					break;
				}
			MapData.DataIsland.set(center, island);
		});

		// IslandBuilder calculates whether a cell is a lake cell by means that:
		// 1. Floodfill every border cell so that the the OCEAN region is determined.
		// 2. Search for any not floodfill-ed OCEAN cell and set it to be LAKE.
		input.getCenters().parallelStream().filter(center ->
				MapData.DataBorder.get(center) == MapData.DataBorder.BORDER)
				.forEach(center -> floodfill(input, center));
		input.getCenters().parallelStream().filter(center ->
				MapData.DataIsland.get(center) == MapData.DataIsland.OCEAN &&
						MapData.DataAny.get(center, FLOODFILLED_KEY) == null)
				.forEach(center -> MapData.DataIsland.set(center,
						MapData.DataIsland.LAKE));
		input.getCenters().parallelStream().forEach(center ->
				MapData.DataAny.remove(center, FLOODFILLED_KEY));

		// find lake corners
		input.getCorners().parallelStream()
				.filter(corner -> MapData.DataIsland.get(corner) ==
						MapData.DataIsland.OCEAN)
				.filter(corner -> MapData.DataIsland.get(corner.s1) ==
						MapData.DataIsland.LAKE &&
						MapData.DataIsland.get(corner.s2) == MapData.DataIsland.LAKE &&
						MapData.DataIsland.get(corner.s3) == MapData.DataIsland.LAKE)
				.forEach(corner -> MapData.DataIsland.set(corner,
						MapData.DataIsland.LAKE));

		// find coastal corners
		input.getCorners().parallelStream().forEach(corner -> {
			boolean i1 = MapData.DataIsland.get(corner.s1) == MapData.DataIsland.LAND;
			boolean i2 = MapData.DataIsland.get(corner.s2) == MapData.DataIsland.LAND;
			boolean i3 = MapData.DataIsland.get(corner.s3) == MapData.DataIsland.LAND;
			int sum = (i1 ? 1 : 0) + (i2 ? 1 : 0) + (i3 ? 1 : 0);
			if (sum > 0 && sum < 3)
				MapData.DataIsland.set(corner, MapData.DataIsland.COAST);
			else if (sum == 0)
				MapData.DataIsland.set(corner, MapData.DataIsland.OCEAN);
		});

		return input;
	}

	// temporary keys used in floodfilling
	private static final String FLOODFILLED_KEY = "floodfilled";

	private void floodfill(Map map, Map.Center cell) {
		// Floodfill uses BFS to avoid stack overflow
		// ( in case number of cells may become very big )
		BFSQueue<Map.Center> queue = new BFSQueue<>();
		if (MapData.DataAny.get(cell, FLOODFILLED_KEY) == null)
			queue.offer(cell);
		MapData.DataIsland data = MapData.DataIsland.get(cell);
		while (!queue.isEmpty()) {
			final Map.Center center = queue.poll();
			MapData.DataAny.set(center, FLOODFILLED_KEY, true);
			center.edges.parallelStream()
					.map(edge -> edge.otherCenter(center))
					.filter(center2 -> MapData.DataIsland.get(center2) == data &&
							MapData.DataAny.get(center2, FLOODFILLED_KEY) == null)
					.forEach(queue::offer);
		}
	}
}