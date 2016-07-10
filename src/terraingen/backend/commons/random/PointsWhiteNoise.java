package terraingen.backend.commons.random;

import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.Point;
import terraingen.backend.commons.PointBox;
import terraingen.backend.nodegraph.IProcessor;
import terraingen.backend.nodegraph.ISupplier;

import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 *
 */
public class PointsWhiteNoise implements IProcessor<Long, PointBox>,
		ISupplier<PointBox> {
	protected Boundaries boundaries;
	protected int pointCount;

	public PointsWhiteNoise(Boundaries boundaries, int pointCount) {
		this.boundaries = boundaries;
		this.pointCount = pointCount;
	}

	@Override
	public PointBox process(Long seed) {
		Random random = new Random(seed);
		double width = this.boundaries.right - this.boundaries.left;
		double height = this.boundaries.bottom - this.boundaries.top;

		List<Point> points = new Vector<>();
		for (int i = 0; i < this.pointCount; ++i) {
			double x = random.nextDouble() * width + this.boundaries.left;
			double y = random.nextDouble() * height + this.boundaries.top;
			points.add(new Point(x, y));
		}

		return new PointBox(this.boundaries, points);
	}

	@Override
	public PointBox supply() {
		return this.process(System.currentTimeMillis());
	}
}
