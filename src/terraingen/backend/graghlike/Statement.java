package terraingen.backend.graghlike;

import java.util.ArrayList;
import java.util.List;

/**
 * Statement that have single / multiple head and tail, class set to abstract that
 * it cannot be instantiated.
 */
public abstract class Statement<I, O> {
	protected boolean executed = false;

	protected List<IInput<I>> inputCollection;
	protected List<IOutput<O>> outputCollection;

	public Statement() {
		this.inputCollection = new ArrayList<>();
		this.outputCollection = new ArrayList<>();
	}

	public List<IInput<I>> getInputs() {
		return this.inputCollection;
	}

	public List<IOutput<O>> getOutputs() {
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
	 * because of wrong usage of the whole structure, like: unassigned edge, edgeless
	 * head / tail, algorithm error, wrong head etc.. Anyone who works on it should
	 * be aware of what would happen while calling this method, thus making it
	 * exception-free with his / her own brain.
	 */
	public void execute() {
		this.executed = true;
	}
}
