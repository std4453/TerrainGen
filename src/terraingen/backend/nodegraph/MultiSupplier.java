package terraingen.backend.nodegraph;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MultiSupplier<I, O> implements ISupplier<O> {
	protected List<SupplierNode<I>> suppliers;
	protected List<Edge<I>> combinerInputs;
	protected MultiCombinerNode<I, O> combiner;
	protected Edge<O> output;

	public MultiSupplier(
			List<SupplierNode<I>> suppliers,
			MultiCombinerNode<I, O> combiner) {
		this.suppliers = suppliers;
		this.combiner = combiner;

		int size = suppliers.size();
		this.combinerInputs = new ArrayList<>();
		for (int i = 0; i < size; ++i)
			this.combinerInputs.add(new Edge<>(null, combiner.getInput(i)));
		new Edge<>(combiner.getOutput(), null);
	}

	@Override
	public O supply() {
		int size = this.suppliers.size();
		for (int i = 0; i < size; ++i)
			this.combinerInputs.get(i).setValue(Executor.execute(this.suppliers.get(i)));
		this.combiner.execute();
		return this.combiner.getOutput().getInEdge().getValue();
	}
}
