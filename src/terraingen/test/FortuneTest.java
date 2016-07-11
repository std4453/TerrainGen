package terraingen.test;

import org.ajwerner.voronoi.VoronoiAdapter;
import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.PointBox;
import terraingen.backend.commons.random.PointsWhiteNoise;
import terraingen.backend.commons.voronoi.Fortune;
import terraingen.backend.commons.voronoi.VoronoiBox;
import terraingen.backend.commons.voronoi.VoronoiRenderer;
import terraingen.backend.nodegraph.Edge;
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
		final int points = 100;
		final long seed = 10;
//		final Boundaries boundaries = new Boundaries(0, 100, 0, 100);
		final Boundaries boundaries = new Boundaries(-10, 10, -10, 10);

		ProcessorNode<Long, PointBox> randomPoints = new ProcessorNode<>(
				new PointsWhiteNoise(boundaries, points));
		ProcessorNode<PointBox, VoronoiBox> voronoi = null;
		if (true)
			voronoi = new ProcessorNode<>(new Fortune());
		else
			voronoi = new ProcessorNode<>(new VoronoiAdapter());
		ProcessorNode<VoronoiBox, BufferedImage> renderer = new ProcessorNode<>(
				new VoronoiRenderer());
		new Edge<>(randomPoints.getOutput(), voronoi.getInput());
		new Edge<>(voronoi.getOutput(), renderer.getInput());
		Statement<Long, BufferedImage> pipeline = new Statement<>(randomPoints.getInput(),
				renderer.getOutput());

		BufferedImage image = Executor.execute(pipeline, seed);
		DialogSingleImage.instance.show(image);
	}
}
