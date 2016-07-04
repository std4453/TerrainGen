package terraingen.backend.nodegraph;

/**
 * Wrapper statement: {@link ISupplier}<br />
 * Theoretically an instance of SupplierStatement won't be executed during the
 * execution of an {@link Clause}, since it has no head edges, therefore instances of
 * SupplierStatement under a {@link Clause} is collected when the {@link Clause} is
 * being built, and their {@code execute()} method is invoked and their tail edges are
 * set.
 */
public class SupplierStatement<O> extends Statement<Object, O> {
	protected ISupplier<O> supplier;
	protected IOutput<O> output;

	public SupplierStatement(ISupplier<O> supplier) {
		this.supplier = supplier;

		this.output = new IOutput<>(this);
		this.outputCollection.add(this.output);
	}

	public IOutput<O> getOutput() {
		return this.output;
	}

	@Override
	public void execute() {
		super.execute();

		O output = this.supplier.supply();
		this.output.getInEdge().setValue(output);
	}
}
