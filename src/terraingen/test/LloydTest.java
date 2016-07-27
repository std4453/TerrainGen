package terraingen.test;

import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.PointBox;
import terraingen.backend.commons.noise.PointsWhiteNoise;
import terraingen.backend.commons.voronoi.Fortune;
import terraingen.backend.commons.voronoi.Lloyd;
import terraingen.backend.commons.voronoi.VoronoiBox;
import terraingen.backend.commons.voronoi.VoronoiRenderer;
import terraingen.backend.nodegraph.*;
import terraingen.frontend.dialog.DialogSingleImage;

import java.awt.image.BufferedImage;

public class LloydTest {
	public static void main(String[] args) {
		ProcessorNode<VoronoiBox, PointBox> lloyd = new ProcessorNode<>(new Lloyd());
		ProcessorNode<PointBox, VoronoiBox> voronoi = new ProcessorNode<>(new Fortune());
		new Edge<>(lloyd.getOutput(), voronoi.getInput());
		Statement<VoronoiBox, VoronoiBox> whole = new Statement<>
				(lloyd.getInput(), voronoi.getOutput());

		final int points = 100;
		final long seed = 4453;
		Boundaries boundaries = new Boundaries(0, 100, 0, 100);
		ProcessorNode<Long, PointBox> random = new ProcessorNode<>(new PointsWhiteNoise
				(boundaries, points));
		ProcessorNode<PointBox, VoronoiBox> voronoi2 = new ProcessorNode<>(new Fortune());
		new Edge<>(random.getOutput(), voronoi2.getInput());
		VoronoiBox voronoiBox = Executor.execute(new Statement<>(random.getInput(),
				voronoi2.getOutput()), seed);

		RepeatClause<VoronoiBox> repeatClause = new RepeatClause<>(10, whole);
		VoronoiBox lloydResult = Executor.execute(new Statement<>(repeatClause),
				voronoiBox);

		BufferedImage image = Executor.execute(new Statement<>(new ProcessorNode<>(new
				VoronoiRenderer())), lloydResult);
		DialogSingleImage.instance.show(image);
	}
}
