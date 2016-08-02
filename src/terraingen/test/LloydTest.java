package terraingen.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.noise.PointsWhiteNoise;
import terraingen.backend.commons.voronoi.Fortune;
import terraingen.backend.commons.voronoi.Lloyd;
import terraingen.backend.commons.voronoi.VoronoiBox;
import terraingen.backend.commons.voronoi.VoronoiRenderer;
import terraingen.backend.nodegraph.*;
import terraingen.frontend.dialog.DialogSingleImage;

import java.awt.image.BufferedImage;

import static terraingen.backend.nodegraph.NodeGraphHelper.create;
import static terraingen.backend.nodegraph.NodeGraphHelper.embrace;
import static terraingen.backend.nodegraph.NodeGraphHelper.embraceSupplier;

public class LloydTest {
	private static final Log log = LogFactory.getLog(LloydTest.class);

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		final int points = 1000;
		final int lloydRepeats = 50;
		final int repeats = 50;

		SupplierNode<VoronoiBox> lloyd = embraceSupplier(
				create(() -> 4453L),
				create((IProcessor) new PointsWhiteNoise(new Boundaries(100, 100),
						points)),
				create(new Fortune()),
				new RepeatClause(lloydRepeats, (Statement<VoronoiBox, VoronoiBox>)
						embrace(create(new Lloyd()), create(new Fortune()))));
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < repeats; ++i)
			Executor.execute(lloyd);
		long endTime = System.currentTimeMillis();

		log.info(String.format("Points: %d, Lloyd: %d, Repeats: %d, Time: %dms",
				points, lloydRepeats, repeats, (endTime - startTime)));

		DialogSingleImage.instance.show(Executor.execute(
				(SupplierNode<BufferedImage>) embrace(
						create(() -> 4453L),
						create((IProcessor) new PointsWhiteNoise(new Boundaries(100, 100),
								100)),
						create(new Fortune()),
						new RepeatClause(50, (Statement<VoronoiBox, VoronoiBox>)
								embrace(create(new Lloyd()), create(new Fortune()))),
						create(new VoronoiRenderer())
				)));
	}
}
