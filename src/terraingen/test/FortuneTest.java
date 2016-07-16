package terraingen.test;

import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.PointBox;
import terraingen.backend.commons.random.PointsWhiteNoise;
import terraingen.backend.commons.voronoi.Fortune;
import terraingen.backend.commons.voronoi.VoronoiBox;
import terraingen.backend.commons.voronoi.VoronoiRenderer;
import terraingen.backend.nodegraph.Executor;
import terraingen.backend.nodegraph.ProcessorNode;
import terraingen.backend.nodegraph.Statement;
import terraingen.frontend.dialog.DialogSingleImage;

import java.awt.image.BufferedImage;

/**
 *
 */
public class FortuneTest {
	public static void main(String[] args) {
		final int points = 1000;
		final long seed = 4453;
		final Boundaries boundaries = new Boundaries(0, 100, 0, 100);

		ProcessorNode<Long, PointBox> randomPoints = new ProcessorNode<>(
				new PointsWhiteNoise(boundaries, points));
		ProcessorNode<PointBox, VoronoiBox> voronoi = new ProcessorNode<>(new Fortune());
		ProcessorNode<VoronoiBox, BufferedImage> renderer = new ProcessorNode<>(
				new VoronoiRenderer());

		// profiler components
		Statement<Long, PointBox> pointsGenerator = new Statement<>(randomPoints);
		PointBox pointBox = Executor.execute(pointsGenerator, seed);
		Statement<PointBox, VoronoiBox> voronoiGenerator = new Statement<>(voronoi);

		// profiler
		long startTime = System.currentTimeMillis();
		final int repeats = 1000;
		for (int i = 0; i < repeats; ++i)
			Executor.execute(voronoiGenerator, pointBox);
		long endTime = System.currentTimeMillis();

		System.out.println(
				String.format("Points: %d, Repeats: %d, Time: %dms", points, repeats,
						(endTime - startTime)));

		// display image
		VoronoiBox voronoiBox = Executor.execute(voronoiGenerator, pointBox);
		Statement<VoronoiBox, BufferedImage> rendererS = new Statement<>(renderer);
		BufferedImage image = Executor.execute(rendererS, voronoiBox);
		DialogSingleImage.instance.show(image);
	}
}
