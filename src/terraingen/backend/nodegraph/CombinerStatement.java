package terraingen.backend.nodegraph;

/**
 * Wrapper statement: {@link ICombiner}
 */
public class CombinerStatement<I, O> extends Statement<I, O> {
	protected ICombiner<I, O> combiner;
	protected IInput<I> input1;
	protected IInput<I> input2;
	protected IOutput<O> output;

	/**
	 * Non-null combiner is not accepted.
	 */
	public CombinerStatement(ICombiner<I, O> combiner) {
		this.combiner = combiner;

		this.input1 = new IInput<>(this);
		this.input2 = new IInput<>(this);
		this.output = new IOutput<>(this);
		this.inputCollection.add(this.input1);
		this.inputCollection.add(this.input2);
		this.outputCollection.add(this.output);
	}

	public IInput<I> getInput1() {
		return this.input1;
	}

	public IInput<I> getInput2() {
		return this.input2;
	}

	public IOutput<O> getOutput() {
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
