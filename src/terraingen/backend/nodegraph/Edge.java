package terraingen.backend.nodegraph;

/**
 * Edge that passes on a value of class V
 */
public class Edge<V> {
	protected V value = null;
	protected boolean valueSet = false;
	protected IInput<V> input;
	protected IOutput<V> output;

	public Edge(IInput<V> input, IOutput<V> output) {
		this.input = input;
		this.output = output;

		if (input != null)
			input.setOutEdge(this);
		if (output != null)
			output.setInEdge(this);
	}

	public Edge(IOutput<V> output, IInput<V> input) {
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

	public IInput<V> getInput() {
		return this.input;
	}

	public void setInput(IInput<V> input) {
		this.input = input;
	}

	public IOutput<V> getOutput() {
		return this.output;
	}

	public void setOutput(IOutput<V> output) {
		this.output = output;
	}

	public void clearValue() {
		this.valueSet = false;
		this.value = null;
	}
}
