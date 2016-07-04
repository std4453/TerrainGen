package terraingen.backend.nodegraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Node that have single / multiple input and output, class set to abstract that
 * it cannot be instantiated.
 */
public abstract class Node<I, O> {
	protected boolean executed = false;

	protected List<InputPort<I>> inputCollection;
	protected List<OutputPort<O>> outputCollection;

	public Node() {
		this.inputCollection = new ArrayList<>();
		this.outputCollection = new ArrayList<>();
	}

	public List<InputPort<I>> getInputs() {
		return this.inputCollection;
	}

	public List<OutputPort<O>> getOutputs() {
		return this.outputCollection;
	}

	public boolean isExecuted() {
		return this.executed;
	}

	public void clearExecuted() {
		this.executed = false;
	}

	/**
	 * Default method, implementations should override it. However, it is strongly
	 * recommended that {@code super.execute()} is called in any further implementation
	 * of this method.<br /><
	 * <br />
	 * Note that this method is not exception-free, any kind of exception can be thrown
	 * because of wrong usage of the whole model, like: unassigned edge, edgeless
	 * input / output, algorithm error, wrong input etc.. Anyone who works on it should
	 * be aware of what would happen while calling this method, thus making it
	 * exception-free with his / her own brain.
	 */
	public void execute() {
		this.executed = true;
	}
}
