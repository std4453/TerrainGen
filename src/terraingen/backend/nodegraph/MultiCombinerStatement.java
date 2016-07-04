package terraingen.backend.nodegraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper statement: {@link IMultiCombiner}
 */
public class MultiCombinerStatement<I, O> extends Statement<I, O> {
	protected IMultiCombiner<I, O> multiCombiner;
	protected IOutput<O> output;

	public MultiCombinerStatement(IMultiCombiner<I, O> multiCombiner, int inputCount) {
		this.multiCombiner = multiCombiner;

		this.output = new IOutput<>(this);
		this.outputCollection.add(this.output);
		for (int i = 0; i < inputCount; ++i) {
			IInput<I> input = new IInput<>(this);
			this.inputCollection.add(input);
		}
	}

	public IInput<I> getInput(int n) {
		return this.inputCollection.get(n);
	}

	public IOutput<O> getOutput() {
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
