package terraingen.backend.nodegraph;

/**
 * input: v, output: v
 */
public class BlankNode<V> extends Node<V, V> {
	protected IInput<V> input;
	protected IOutput<V> output;

	public BlankNode() {
		this.input = new IInput<>(this);
		this.output = new IOutput<>(this);
		this.inputCollection.add(this.input);
		this.outputCollection.add(this.output);
	}

	@Override
	public void execute() {
		super.execute();

		V value = this.input.getOutEdge().getValue();
		this.output.getInEdge().setValue(value);
	}
}
