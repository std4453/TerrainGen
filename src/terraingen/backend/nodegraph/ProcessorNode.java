package terraingen.backend.nodegraph;

/**
 * Wrapper node: {@link IProcessor}
 */
public class ProcessorNode<I, O> extends Node<I, O> {
	protected IProcessor<I, O> processor;
	protected IInput<I> input;
	protected IOutput<O> output;

	public ProcessorNode(IProcessor<I, O> processor) {
		this.processor = processor;

		this.input = new IInput<>(this);
		this.output = new IOutput<>(this);
		this.inputCollection.add(this.input);
		this.outputCollection.add(this.output);
	}

	public IInput<I> getInput() {
		return this.input;
	}

	public IOutput<O> getOutput() {
		return this.output;
	}

	@Override
	public void execute() {
		super.execute();

		I input = this.input.getOutEdge().getValue();
		O output = this.processor.process(input);
		this.output.getInEdge().setValue(output);
	}
}
