package terraingen.backend.nodegraph;

/**
 *
 */
public class CompositeConsumer<I, O> implements IConsumer<I> {
	protected ConsumerNode<O> consumer;
	protected Statement<I, O> body;

	public CompositeConsumer(ConsumerNode<O> consumer,
							 Statement<I, O> body) {
		this.consumer = consumer;
		this.body = body;
	}

	@Override
	public void consume(I input) {
		O output = Executor.execute(this.body, input);
		Executor.execute(this.consumer, output);
	}
}
