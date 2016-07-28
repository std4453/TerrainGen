package terraingen.backend.commons.grid;

import terraingen.backend.nodegraph.Executor;
import terraingen.backend.nodegraph.IProcessor;
import terraingen.backend.nodegraph.Statement;

/**
 * Maps a {@link Grid} with a mapper.
 */
public class GridMapper implements IProcessor<Grid, Grid> {
	protected Statement<Double, Double> mapper;

	public GridMapper(
			Statement<Double, Double> mapper) {
		this.mapper = mapper;
	}

	@Override
	public Grid process(Grid input) {
		int width = input.getWidth();
		int height = input.getHeight();
		double data[][] = new double[width][height];
		for (int i = 0; i < width; ++i)
			for (int j = 0; j < height; ++j)
				data[i][j] = Executor.execute(this.mapper, input.get(i, j));
		return new Grid(data);
	}
}
