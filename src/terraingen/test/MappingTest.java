package terraingen.test;

import terraingen.backend.algorithms.amitp.Map;
import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.noise.PointsWhiteNoise;
import terraingen.backend.commons.voronoi.Fortune;
import terraingen.backend.commons.voronoi.VoronoiBox;
import terraingen.backend.nodegraph.Executor;
import terraingen.backend.nodegraph.IProcessor;
import terraingen.backend.nodegraph.SupplierNode;

import static terraingen.backend.nodegraph.NodeGraphHelper.create;
import static terraingen.backend.nodegraph.NodeGraphHelper.embrace;

/**
 *
 */
public class MappingTest {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		final long seed = 4453L;
		final int points = 3000;

		VoronoiBox voronoiBox = Executor.execute((SupplierNode<VoronoiBox>) embrace(
				create(() -> seed),
				create((IProcessor) new PointsWhiteNoise(new Boundaries(1000, 1000),
						points)),
				create(new Fortune())
		));

		int repeats = 1000;
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < repeats; ++i)
			new Map(voronoiBox);
		long endTime = System.currentTimeMillis();

		System.out.println("Points: " + points + " Repeats: " + repeats + " Time: " +
				(endTime - startTime) + "ms");
	}
}
