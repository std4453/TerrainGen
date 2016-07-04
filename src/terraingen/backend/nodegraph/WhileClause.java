package terraingen.backend.nodegraph;

/**
 * The while clause takes in an input and iterates until {@code this.interrupter}
 * returns false, like the C / Java while clause.<br />
 * Note that the input and the final output is of the same class, though may change
 * within the iteration. To create a traditional for-clause with a separate iterator like
 * repeating a certain action 100 times, you should use {@link ForClause}, or use an
 * Array which contains both the input value and the separate iterator.
 */
public class WhileClause<V> extends Node<V, V> {
	protected Statement<V, Boolean> interrupter;
	protected Statement<V, V> body;

	protected InputPort<V> input;
	protected OutputPort<V> output;

	public WhileClause(Statement<V, Boolean> interrupter, Statement<V, V> body) {
		this.interrupter = interrupter;
		this.body = body;

		this.input = new InputPort<>(this);
		this.output = new OutputPort<>(this);
	}

	public InputPort<V> getInput() {
		return this.input;
	}

	public OutputPort<V> getOutput() {
		return this.output;
	}

	@Override
	public void execute() {
		super.execute();

		V value = this.input.getOutEdge().getValue();
		while (Executor.execute(this.interrupter, value)) {
			value = Executor.execute(this.body, value);
		}

		this.output.getInEdge().setValue(value);
	}
}
