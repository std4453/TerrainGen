package terraingen.backend.nodegraph;

/**
 * Consumer consumes an input of class I
 */
public interface IConsumer<I> {
	void consume(I input);
}
