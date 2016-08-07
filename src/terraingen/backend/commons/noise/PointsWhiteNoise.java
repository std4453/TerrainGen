package terraingen.backend.commons.noise;

import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.Point;
import terraingen.backend.commons.PointBox;
import terraingen.backend.nodegraph.IProcessor;
import terraingen.backend.nodegraph.ISupplier;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generates a {@link PointBox} with completely random points, i.e., white noise.
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

		List<Point> points = Stream.iterate(0, n -> n + 1).limit(this.pointCount)
				.parallel().map(n -> {
					double x = random.nextDouble() * width + this.boundaries.left;
					double y = random.nextDouble() * height + this.boundaries.top;
					return new Point(x, y);
				}).collect(Collectors.toList());

		return new PointBox(this.boundaries, points);
	}

	@Override
	public PointBox supply() {
		return this.process(System.currentTimeMillis());
	}
}
