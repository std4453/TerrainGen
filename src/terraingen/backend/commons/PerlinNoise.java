package terraingen.backend.commons;

import java.util.Random;

import static terraingen.utils.MathUtils.floor;

/**
 * My implementation of
 * <a href="https://en.wikipedia.org/wiki/Perlin_noise"><i>Perlin Noise</i></a><br />
 * In fact, this implementation is a direct translation from
 * <a href="http://webstaff.itn.liu.se/~stegu/simplexnoise/simplexnoise.pdf">
 * this paper</a>
 */
public class PerlinNoise {
	private static double[][] gradients = {
			{-1, -1}, {-1, 0}, {-1, 1},
			{0, -1}, {0, 1},
			{1, -1}, {1, 0}, {1, 1},
	};
	private static int permutation[] = new int[256];

	static {
		Random random = new Random(4453);
		for (int i = 0; i < 256; ++i)
			permutation[i] = random.nextInt(256);
	}

	public static double noise(double x, double y) {
		int i = floor(x), j = floor(y);
		double u = x - i, v = y - j;
		double g00[] = determineGradient(i, j),
				g01[] = determineGradient(i, j + 1),
				g10[] = determineGradient(i + 1, j),
				g11[] = determineGradient(i + 1, j + 1);
		double n00 = dot(g00, u, v),
				n01 = dot(g01, u, v - 1),
				n10 = dot(g10, u - 1, v),
				n11 = dot(g11, u - 1, v - 1);
		double fu = fade(u), fv = fade(v), omfu = 1 - fu, omfv = 1 - fv;
		double nx0 = omfu * n00 + fu * n10,
				nx1 = omfu * n01 + fu * n11;
		return omfv * nx0 + fv * nx1;
	}

	/**
	 * Calculates the blending function with Horner's method
	 */
	private static double fade(double t) {
		return ((6 * t - 15) * t + 10) * t * t * t;
	}

	private static double dot(double a[], double x, double y) {
		return a[0] * x + a[1] * y;
	}

	private static double[] determineGradient(int x, int y) {
		return gradients[permutation[(permutation[x & 255] + y) & 255] % gradients
				.length];
	}
}
