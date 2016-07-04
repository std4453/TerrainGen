package terraingen.backend.nodegraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper node: {@link IMultiCombiner}
 */
public class MultiCombinerNode<I, O> extends Node<I, O> {
	protected IMultiCombiner<I, O> multiCombiner;
	protected OutputPort<O> output;

	public MultiCombinerNode(IMultiCombiner<I, O> multiCombiner, int inputCount) {
		this.multiCombiner = multiCombiner;

		this.output = new OutputPort<>(this);
		for (int i = 0; i < inputCount; ++i)
			new InputPort<>(this);
	}

	public InputPort<I> getInput(int n) {
		return this.inputCollection.get(n);
	}

	public OutputPort<O> getOutput() {
		return this.output;
	}

	@Override
	public void execute() {
		super.execute();

		List<I> inputs = new ArrayList<>();
		int size = this.inputCollection.size();
		for (int i = 0; i < size; ++i)
			inputs.add(this.getInput(i).getOutEdge().getValue());
		O output = this.multiCombiner.combine(inputs);

		this.output.getInEdge().setValue(output);
	}
}
