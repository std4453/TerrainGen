package terraingen.backend.nodegraph;

/**
 * {@code IfClause} selects between 2 routes according to what an conditioner statement
 * returns. The conditioner should take the input of the if-clause as input and output
 * an boolean value, while {@code true} means the first route ( {@code route1}: if-then
 * ), and false the second one ( {@code route2}: if-else )
 */
public class IfClause<I, O> extends Node<I, O> implements IProcessorLike<I, O> {
	protected Statement<I, Boolean> conditioner;
	protected Statement<I, O> route1;
	protected Statement<I, O> route2;

	protected Edge<I> route1Head;
	protected Edge<I> route2Head;
	protected Edge<O> route1Tail;
	protected Edge<O> route2Tail;

	protected Edge<I> conditionerHead;
	protected Edge<Boolean> conditionerTail;

	protected InputPort<I> input;
	protected OutputPort<O> output;

	public IfClause(Statement<I, Boolean> conditioner, Statement<I, O> route1,
					Statement<I, O>
							route2) {
		this.conditioner = conditioner;
		this.route1 = route1;
		this.route2 = route2;

		// bind half edges
		this.route1Head = new Edge<>(null, this.route1.getInput());
		this.route2Head = new Edge<>(null, this.route2.getInput());
		this.route1Tail = new Edge<>(this.route1.getOutput(), null);
		this.route2Tail = new Edge<>(this.route2.getOutput(), null);
		this.conditionerHead = new Edge<>(null, this.conditioner.getInput());
		this.conditionerTail = new Edge<>(this.conditioner.getOutput(), null);

		// external input & output
		this.input = new InputPort<>(this);
		this.output = new OutputPort<>(this);
		this.inputCollection.add(this.input);
		this.outputCollection.add(this.output);
	}

	@Override
	public InputPort<I> getInput() {
		return this.input;
	}

	@Override
	public OutputPort<O> getOutput() {
		return this.output;
	}

	@Override
	public void execute() {
		super.execute();

		I input = this.input.getOutEdge().getValue();
		Boolean choice = Executor.execute(this.conditioner, input);
		Statement<I, O> route = choice ? this.route1 : this.route2;
		O output = Executor.execute(route, input);

		this.output.getInEdge().setValue(output);
	}
}