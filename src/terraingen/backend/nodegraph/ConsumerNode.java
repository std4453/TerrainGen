package terraingen.backend.nodegraph;

/**
 * Wrapper Node: {@link IConsumer}<br />
 * Output value will not be set
 */
public class ConsumerNode<I> extends Node<I, Object> {
	protected IConsumer<I> consumer;
	protected InputPort<I> input;

	public ConsumerNode(IConsumer<I> consumer) {
		this.consumer = consumer;

		this.input = new InputPort<>(this);
	}

	public InputPort<I> getInput() {
		return this.input;
	}

	@Override
	public void execute() {
		super.execute();

		I input = this.input.getOutEdge().getValue();
		this.consumer.consume(input);
	}
}
