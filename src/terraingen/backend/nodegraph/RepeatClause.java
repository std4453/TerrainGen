package terraingen.backend.nodegraph;

/**
 *
 */
public class RepeatClause<V> extends ForClause<Integer, V> {
	private static Statement<Integer, Boolean> interrupter = new Statement<>(
			new ProcessorNode<>(new IProcessor<Integer, Boolean>() {
				@Override
				public Boolean process(Integer input) {
					return input > 0;
				}
			}));
	private static Statement<Integer, Integer> iteration = new Statement<>(
			new ProcessorNode<>(new IProcessor<Integer, Integer>() {
				@Override
				public Integer process(Integer input) {
					return input - 1;
				}
			}));

	public RepeatClause(int repeats, Statement<V, V> body) {
		super(new SupplierNode<>(new SimpleSupplier<>(repeats)), interrupter, iteration,
				body);
	}
}
