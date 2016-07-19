package terraingen.backend.nodegraph;

/**
 * {@code CompositeSupplier} works like {@link Executor#execute(Statement, Object)},
 * which is an {@link ISupplier} and can be used in the node graph.
 */
public class CompositeSupplier<I, O> implements ISupplier<O> {
	protected SupplierNode<I> input;
	protected Statement<I, O> body;

	public CompositeSupplier(SupplierNode<I> input,
							 Statement<I, O> body) {
		this.input = input;
		this.body = body;
	}

	@Override
	public O supply() {
		I input = Executor.execute(this.input);
		return Executor.execute(this.body, input);
	}
}
