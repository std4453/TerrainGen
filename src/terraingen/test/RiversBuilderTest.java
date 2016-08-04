package terraingen.test;

import terraingen.backend.algorithms.amitp.*;
import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.noise.PointsWhiteNoise;
import terraingen.backend.commons.voronoi.Fortune;
import terraingen.backend.commons.voronoi.Lloyd;
import terraingen.backend.commons.voronoi.VoronoiBox;
import terraingen.backend.nodegraph.*;
import terraingen.frontend.dialog.DialogSingleImage;

import java.awt.image.BufferedImage;

import static terraingen.backend.nodegraph.NodeGraphHelper.create;
import static terraingen.backend.nodegraph.NodeGraphHelper.embrace;

/**
 *
 */
public class RiversBuilderTest {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		final long seed = 4453;
		DialogSingleImage.instance.show(
				Executor.execute((SupplierNode<BufferedImage>) embrace(
						create(() -> seed),
						create((IProcessor) new PointsWhiteNoise(new Boundaries(1000,
								1000), 3000)),
						create(new Fortune()),
						new RepeatClause(50,
								(Statement<VoronoiBox, VoronoiBox>) embrace(create
										(new Lloyd()), create(new Fortune()))),
						create(Map.mapConverter),
						create(new IslandBuilder(seed)),
						create(new ElevationBuilder()),
						create(new RiversBuilder(seed, 300)),
						create(new MapRenderer(MapRenderer.Type.ELEVATION_AND_RIVERS))
				)));
	}
}
