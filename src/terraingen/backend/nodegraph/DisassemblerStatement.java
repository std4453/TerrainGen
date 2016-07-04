package terraingen.backend.nodegraph;

import java.util.List;

/**
 * Wrapper statement: {@link IDisassembler}
 */
public class DisassemblerStatement<I, O> extends Statement<I, O> {
	protected IDisassembler<I, O> disassembler;
	protected IInput<I> input;

	public DisassemblerStatement(IDisassembler<I, O> disassembler, int outputCount) {
		this.disassembler = disassembler;

		this.input = new IInput<>(this);
		this.inputCollection.add(this.input);
		for (int i = 0; i < outputCount; ++i) {
			IOutput<O> output = new IOutput<>(this);
			this.outputCollection.add(output);
		}
	}

	public IInput<I> getInput() {
		return this.input;
	}

	public IOutput<O> getOutput(int n) {
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
