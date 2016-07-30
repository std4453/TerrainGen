package terraingen.backend.nodegraph;

/**
 *
 */
public class RepeatClause<V> extends ForClause<Integer, V> {
	private static Statement<Integer, Boolean> interrupter = new Statement<>(
			new ProcessorNode<>((IProcessor<Integer, Boolean>) input1 -> input1 > 0));
	private static Statement<Integer, Integer> iteration = new Statement<>(
			new ProcessorNode<>((IProcessor<Integer, Integer>) input1 -> input1 - 1));

	public RepeatClause(int repeats, Statement<V, V> body) {
		super(new SupplierNode<>(new SimpleSupplier<>(repeats)), interrupter, iteration,
				body);
	}
}
