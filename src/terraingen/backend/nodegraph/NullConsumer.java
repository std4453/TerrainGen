package terraingen.backend.nodegraph;

/**
 * Does nothing.
 */
public class NullConsumer<I> implements IConsumer<I> {
	@Override
	public void consume(I input) {
	}
}
