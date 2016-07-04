package terraingen.backend.nodegraph;

/**
 * Wrapper node: {@link ICombiner}
 */
public class CombinerNode<I, O> extends Node<I, O> {
	protected ICombiner<I, O> combiner;
	protected InputPort<I> input1;
	protected InputPort<I> input2;
	protected OutputPort<O> output;

	/**
	 * Null combiner is not accepted.
	 */
	public CombinerNode(ICombiner<I, O> combiner) {
		this.combiner = combiner;

		this.input1 = new InputPort<>(this);
		this.input2 = new InputPort<>(this);
		this.output = new OutputPort<>(this);
		this.inputCollection.add(this.input1);
		this.inputCollection.add(this.input2);
		this.outputCollection.add(this.output);
	}

	public InputPort<I> getInput1() {
		return this.input1;
	}

	public InputPort<I> getInput2() {
		return this.input2;
	}

	public OutputPort<O> getOutput() {
		return this.output;
	}

	@Override
	public void execute() {
		super.execute();

		I input1 = this.input1.getOutEdge().getValue();
		I input2 = this.input2.getOutEdge().getValue();
		O output = this.combiner.combine(input1, input2);
		this.output.getInEdge().setValue(output);
	}
}
