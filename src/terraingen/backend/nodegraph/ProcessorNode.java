package terraingen.backend.nodegraph;

/**
 * Wrapper node: {@link IProcessor}
 */
public class ProcessorNode<I, O> extends Node<I, O> {
	protected IProcessor<I, O> processor;
	protected InputPort<I> input;
	protected OutputPort<O> output;

	public ProcessorNode(IProcessor<I, O> processor) {
		this.processor = processor;

		this.input = new InputPort<>(this);
		this.output = new OutputPort<>(this);
		this.inputCollection.add(this.input);
		this.outputCollection.add(this.output);
	}

	public InputPort<I> getInput() {
		return this.input;
	}

	public OutputPort<O> getOutput() {
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
