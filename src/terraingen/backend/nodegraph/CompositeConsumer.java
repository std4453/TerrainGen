package terraingen.backend.nodegraph;

/**
 *
 */
public class CompositeConsumer<I, O> implements IConsumer<I> {
	protected IConsumer<O> consumer;
	protected Statement<I, O> body;

	public CompositeConsumer(IConsumer<O> consumer,
							 Statement<I, O> body) {
		this.consumer = consumer;
		this.body = body;
	}

	@Override
	public void consume(I input) {
		O output = Executor.execute(this.body, input);
		this.consumer.consume(output);
	}
}
