package terraingen.backend.commons.random;

import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.Grid;
import terraingen.backend.commons.SimplexNoise;
import terraingen.backend.nodegraph.IProcessor;

import java.util.Random;

import static terraingen.utils.MathUtils.floor;

/**
 * Wrapper of {@link SimplexNoise}, provides features like reproducible randomness & auto
 * conversion to {@link Grid}
 */
public class GridSimplex implements IProcessor<Boundaries, Grid> {
	protected long seed;
	protected double interval;
	protected Random random;

	public GridSimplex(long seed, double interval) {
		this.seed = seed;
		this.interval = interval;

		this.random = new Random(this.seed);
	}

	@Override
	public Grid process(Boundaries input) {
		double x = input.left;
		double y = input.top;
		int width = floor((input.right - x) / this.interval) + 1;
		int height = floor((input.bottom - y) / this.interval) + 1;
		x += this.random.nextInt(32768) - 16384;
		y += this.random.nextInt(32768) - 16384;
		double yInitial = y;

		double data[][] = new double[width][height];
		for (int i = 0; i < width; ++i)
			data[i] = new double[height];

		for (int i = 0; i < width; ++i, x += this.interval) {
			y = yInitial;
			for (int j = 0; j < height; ++j, y += this.interval)
				data[i][j] = SimplexNoise.noise(x, y);
		}

		return new Grid(data);
	}
}
