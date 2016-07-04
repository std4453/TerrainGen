package terraingen.backend.nodegraph;

/**
 * Edge that passes on a value of class V
 */
public class Edge<V> {
	protected V value = null;
	protected boolean valueSet = false;
	protected InputPort<V> input;
	protected OutputPort<V> output;

	public Edge(InputPort<V> input, OutputPort<V> output) {
		this.input = input;
		this.output = output;

		if (input != null)
			input.setOutEdge(this);
		if (output != null)
			output.setInEdge(this);
	}

	public Edge(OutputPort<V> output, InputPort<V> input) {
		this(input, output);
	}

	public boolean isValueSet() {
		return this.valueSet;
	}

	public V getValue() {
		return this.value;
	}

	public void setValue(V value) {
		this.valueSet = true;
		this.value = value;
	}

	public InputPort<V> getInput() {
		return this.input;
	}

	public void setInput(InputPort<V> input) {
		this.input = input;
	}

	public OutputPort<V> getOutput() {
		return this.output;
	}

	public void setOutput(OutputPort<V> output) {
		this.output = output;
	}

	public void clearValue() {
		this.valueSet = false;
		this.value = null;
	}
}
