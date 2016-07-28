package terraingen.backend.commons.grid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A {@code Grid} works like a 2-dimensional array. Notice that a {@code Grid} do not
 * contain any information about where the {@code Grid} locates.
 */
public class Grid {
	private static final Log log = LogFactory.getLog(Grid.class);

	protected double data[][];
	protected int width, height;

	protected double min, max;
	protected boolean minmaxValid = false;

	public Grid(int width, int height) {
		this.width = width;
		this.height = height;
		this.data = new double[width][height];
	}

	/**
	 * For performance reason, data is directly assigned to the instance field (
	 * instead of copying )
	 *
	 * @param data
	 * 		data
	 */
	public Grid(double data[][]) {
		if (data == null) {
			log.warn("Null grid provided, defaulting to empty grid");
			this.width = 0;
			this.height = 0;
			this.data = new double[0][0];
			return;
		}

		this.width = data.length;
		if (data.length == 0) {
			this.height = 0;
			log.warn("Can't infer height of grid, defaulting to 0.");
			this.data = new double[this.width][0];
		} else {
			this.height = data[0].length;
			this.data = data;
		}
	}

	public Grid(Grid grid) {
		this(grid == null ? null : grid.data);
	}

	public boolean inBoundaries(int x, int y) {
		return x >= 0 && x < this.width && y >= 0 && y < this.height;
	}

	public double get(int x, int y) {
		if (!inBoundaries(x, y))
			return Double.NaN;
		return this.data[x][y];
	}

	public void set(int x, int y, double value) {
		if (inBoundaries(x, y)) {
			this.data[x][y] = value;
			this.minmaxValid = false;
		}
	}

	public Grid get(int x, int y, int width, int height) {
		if (!inBoundaries(x, y) || !inBoundaries(x + width - 1, y + height - 1))
			return null;
		double data2[][] = new double[width][height];
		for (int i = 0; i < width; ++i) {
			data2[i] = new double[height];
			System.arraycopy(this.data[i], y, data2[i], 0, height);
		}
		return new Grid(data2);
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public double getMin() {
		if (this.minmaxValid)
			return this.min;

		calcMinMax();
		return this.min;
	}

	public double getMax() {
		if (this.minmaxValid)
			return this.max;

		calcMinMax();
		return this.max;
	}

	protected void calcMinMax() {
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < this.width; ++i)
			for (int j = 0; j < this.height; ++j) {
				double value = get(i, j);
				if (value < min)
					min = value;
				if (value > max)
					max = value;
			}
		this.min = min;
		this.max = max;

		this.minmaxValid = true;
	}
}
