package terraingen.backend.nodegraph;

import java.util.List;

/**
 * Wrapper node: {@link IDisassembler}
 */
public class DisassemblerNode<I, O> extends Node<I, O> {
	protected IDisassembler<I, O> disassembler;
	protected InputPort<I> input;

	public DisassemblerNode(IDisassembler<I, O> disassembler, int outputCount) {
		this.disassembler = disassembler;

		this.input = new InputPort<>(this);
		for (int i = 0; i < outputCount; ++i)
			new OutputPort<>(this);
	}

	public InputPort<I> getInput() {
		return this.input;
	}

	public OutputPort<O> getOutput(int n) {
		return this.outputCollection.get(n);
	}

	@Override
	public void execute() {
		super.execute();

		I input = this.input.getOutEdge().getValue();
		List<O> outputs = this.disassembler.disassemble(input);
		int size = outputs.size();
		for (int i = 0; i < size; ++i) {
			this.getOutput(i).getInEdge().setValue(outputs.get(i));
		}
	}
}
