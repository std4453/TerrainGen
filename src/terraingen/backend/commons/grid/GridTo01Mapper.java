package terraingen.backend.commons.grid;

import static terraingen.backend.nodegraph.NodeGraphHelper.create;
import static terraingen.backend.nodegraph.NodeGraphHelper.embraceStatement;

/**
 * Maps data of a {@link Grid} to range 0 ~ 1
 */
public class GridTo01Mapper extends GridMapper {
	protected double min, max;

	public GridTo01Mapper() {
		super(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Grid process(Grid input) {
		// FIXME: HACK CODE - PROBABLY BUGFUL ( MULTITHREADED )
		final double min = input.getMin();
		final double max = input.getMax();
		final double delta = max - min;
		this.mapper = embraceStatement(create((Double n) -> (n - min) / delta));
		return super.process(input);
	}
}
