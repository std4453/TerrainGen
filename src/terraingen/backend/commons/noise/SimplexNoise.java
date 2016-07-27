package terraingen.backend.commons.noise;

import java.util.Random;

import static terraingen.utils.MathUtils.dot;
import static terraingen.utils.MathUtils.floor;
import static terraingen.utils.MathUtils.square;

/**
 * My implementation of
 * <a href="https://en.wikipedia.org/wiki/Simplex_noise"><i>Simplex Noise</i></a>.<br />
 * Largely referenced from
 * <a href="http://webstaff.itn.liu.se/~stegu/simplexnoise/simplexnoise.pdf">
 * this paper</a>.
 */
public class SimplexNoise {
	// constants
	private static final double skewFactor = (Math.sqrt(3) - 1) / 2;
	private static final double unskewFactor = (3 + Math.sqrt(3)) / 6;
	private static final double p1 = unskewFactor;
	private static final double p2 = 1d / 6 / p1;

	// parameters
	private static double gradients[][] = {
			{1, 1}, {-1, 1}, {1, -1}, {-1, -1},
			{1, 0}, {-1, 0}, {0, 1}, {0, -1},
	};
	private static final long seed = 4453;

	private static int permutation[] = new int[512];

	static {
		Random random = new Random(seed);
		for (int i = 0; i < 256; ++i)
			permutation[i] = random.nextInt(256);
		for (int i = 256; i < 512; ++i)
			permutation[i] = permutation[i & 255];
	}

	public static double noise(final double x, final double y) {
		// algorithm with only 2 multiplications
		double k = (x + y) * skewFactor;
		double m = k + x, n = k + y;
		int i = floor(m), j = floor(n);
		double u = m - i, v = n - j;
		int i1 = u > v ? 1 : 0, j1 = 1 - i1;
		double k1 = (u + v) * unskewFactor;
		double x0 = k1 - v, y0 = k1 - u;
		double x1 = x0 + p2 - i1, y1 = y0 + p2 - j1;
		double x2 = x0 - p1 + p2, y2 = y0 - p1 + p2;

		i &= 255;
		j &= 255;
		double t0 = 0.5 - x0 * x0 - y0 * y0,
				t1 = 0.5 - x1 * x1 - y1 * y1,
				t2 = 0.5 - x2 * x2 - y2 * y2;
		double n0 = influence(t0, dot(determineGradient(i, j), x0, y0)),
				n1 = influence(t1, dot(determineGradient(i + i1, j + j1), x1, y1)),
				n2 = influence(t2, dot(determineGradient(i + 1, j + 1), x2, y2));

		return n0 + n1 + n2; // as can be scaled afterwards, no magic number needed
	}

	private static double[] determineGradient(int x, int y) {
		return gradients[permutation[x + permutation[y]] & 7];
	}

	/**
	 * Ken Perlin's surflet
	 */
	private static double influence(double t, double n) {
		return t > 0 ? square(square(t)) * n : 0;
	}
}
