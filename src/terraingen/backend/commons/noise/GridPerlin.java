package terraingen.backend.commons.noise;

import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.Grid;
import terraingen.backend.nodegraph.IProcessor;

import static terraingen.utils.MathUtils.floor;

/**
 * Wrapper of {@linkplain PerlinNoise PerlinNoise}, provides
 * features like reproducible randomness & auto conversion to {@link Grid}
 */
public class GridPerlin implements IProcessor<Boundaries, Grid> {
	protected double interval;
	protected PerlinNoise noise;

	public GridPerlin(PerlinNoise noise, double interval) {
		if (noise == null)
			noise = PerlinNoise.instance;
		this.noise = noise;
		this.interval = interval;
	}

	public GridPerlin(double interval) {
		this(System.currentTimeMillis(), interval);
	}

	public GridPerlin(long seed, double interval) {
		this(new PerlinNoise(seed), interval);
	}

	@Override
	public Grid process(Boundaries input) {
		double x = input.left;
		double y = input.top;
		int width = floor((input.right - x) / this.interval) + 1;
		int height = floor((input.bottom - y) / this.interval) + 1;
		double yInitial = y;

		double data[][] = new double[width][height];
		for (int i = 0; i < width; ++i)
			data[i] = new double[height];

		for (int i = 0; i < width; ++i, x += this.interval) {
			y = yInitial;
			for (int j = 0; j < height; ++j, y += this.interval)
				data[i][j] = this.noise.noise(x, y);
		}

		return new Grid(data);
	}
}
