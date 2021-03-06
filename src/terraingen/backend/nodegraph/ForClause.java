package terraingen.backend.nodegraph;

/**
 * Pseudo-code: <br />
 * <code><pre>
 * V value = fetchInput();
 * for (I iterator = initializer(); interrupter() == true; iterator =
 * iteration(iterator))
 *     value = body(value);
 * return value;
 * </pre></code>
 */
public class ForClause<I, V> extends Node<V, V> {
	protected SupplierNode<I> initializer;
	protected Statement<I, Boolean> interrupter;
	protected Statement<I, I> iteration;
	protected Statement<V, V> body;
	protected Edge<I> initializerTail;

	protected InputPort<V> input;
	protected OutputPort<V> output;

	public ForClause(SupplierNode<I> initializer,
					 Statement<I, Boolean> interrupter,
					 Statement<I, I> iteration,
					 Statement<V, V> body) {
		this.initializer = initializer;
		this.interrupter = interrupter;
		this.iteration = iteration;
		this.body = body;
		this.initializerTail = new Edge<>(initializer.getOutput(), null);

		this.input = new InputPort<>(this);
		this.output = new OutputPort<>(this);
	}

	public OutputPort<V> getOutput() {
		return this.output;
	}

	public InputPort<V> getInput() {
		return this.input;
	}

	@Override
	public void execute() {
		super.execute();

		V value = this.input.getOutEdge().getValue();

		this.initializer.execute();
		I iterator = this.initializerTail.getValue();
		while (Executor.execute(this.interrupter, iterator)) {
			value = Executor.execute(this.body, value);
			iterator = Executor.execute(this.iteration, iterator);
		}

		this.output.getInEdge().setValue(value);
	}
}
