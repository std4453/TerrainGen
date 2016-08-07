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

	/**
	 * While multithreaded, this method, which override the instance field
	 * {@link #mapper}, will result in the wrong answer. Therefore, in any ( probably )
	 * parallel environment, an instance of {@code GridTo01Mapper} should not be reused.
	 * That is, an new instance of {@code GridTo01Mapper} should be created per usage.
	 */
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
