package terraingen.test;

import terraingen.backend.commons.Boundaries;
import terraingen.backend.commons.GridRenderer;
import terraingen.backend.commons.noise.GridSimplex;
import terraingen.backend.nodegraph.Executor;
import terraingen.frontend.dialog.DialogSingleImage;

import static terraingen.backend.nodegraph.NodeGraphHelper.create;
import static terraingen.backend.nodegraph.NodeGraphHelper.embrace;

/**
 *
 */
public class GridSimplexTest {
	public static void main(String[] args) {
		DialogSingleImage.instance.show(Executor.execute(embrace(
				create(() -> new Boundaries(10, 10)),
				create(new GridSimplex(1, .01)),
				create(new GridRenderer())
		)));
	}
}
