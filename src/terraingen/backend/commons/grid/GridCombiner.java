package terraingen.backend.commons.grid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import terraingen.backend.nodegraph.IMultiCombiner;
import terraingen.utils.Twin;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Adds values of a list of {@link Grid}{@code s} together, with the result's size the
 * minimum of all the inputs.
 */
public class GridCombiner implements IMultiCombiner<Grid, Grid> {
	private static final Log log = LogFactory.getLog(GridCombiner.class);

	@Override
	public Grid combine(List<Grid> inputs) {
		Twin<Integer> minimum = minimumSize(inputs);
		int width = minimum.a, height = minimum.b;

		double data[][] = new double[width][height];
		for (int i = 0; i < width; ++i)
			data[i] = new double[height];

		for (Grid grid : inputs)
			Stream.iterate(0, n -> n + 1).limit(width * height).parallel()
					.forEach(n -> data[n / height][n % height] +=
							grid.get(n / height, n % height));

		return new Grid(data);
	}

	protected Twin<Integer> minimumSize(List<Grid> grids) {
		return new Twin<>(grids.parallelStream().map(Grid::getWidth)
				.collect(Collectors.minBy(Integer::compare)).orElse(0),
				grids.parallelStream().map(Grid::getHeight)
						.collect(Collectors.minBy(Integer::compare)).orElse(0));
	}
}
