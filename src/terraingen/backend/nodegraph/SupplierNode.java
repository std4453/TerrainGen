package terraingen.backend.nodegraph;

/**
 * Wrapper node: {@link ISupplier}<br />
 * Theoretically an instance of SupplierNode won't be executed during the
 * execution of an {@link Statement}, since it has no input edges, therefore instances of
 * SupplierNode under a {@link Statement} is collected when the {@link Statement} is
 * being built, and their {@code execute()} method is invoked and their output edges are
 * set.
 */
public class SupplierNode<O> extends Node<Object, O> {
	protected ISupplier<O> supplier;
	protected OutputPort<O> output;

	public SupplierNode(ISupplier<O> supplier) {
		this.supplier = supplier;

		this.output = new OutputPort<>(this);
	}

	public OutputPort<O> getOutput() {
		return this.output;
	}

	@Override
	public void execute() {
		super.execute();

		O output = this.supplier.supply();
		this.output.getInEdge().setValue(output);
	}
}
