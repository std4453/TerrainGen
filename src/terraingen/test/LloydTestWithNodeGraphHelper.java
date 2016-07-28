package terraingen.test;

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

/**
 * The same as {@link LloydTest}, though, using
 * {@linkplain terraingen.backend.nodegraph.NodeGraphHelper NodeGraphHelper},
 * a lot shorter.
 */
public class LloydTestWithNodeGraphHelper {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
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
