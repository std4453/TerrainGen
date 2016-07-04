package terraingen.backend.nodegraph;

/**
 * input: v, output: v
 */
public class BlankNode<V> extends Node<V, V> {
	protected InputPort<V> input;
	protected OutputPort<V> output;

	public BlankNode() {
		this.input = new InputPort<>(this);
		this.output = new OutputPort<>(this);
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
