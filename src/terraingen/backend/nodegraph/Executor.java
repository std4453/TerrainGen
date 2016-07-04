package terraingen.backend.nodegraph;

/**
 * Execute given {@link Clause} / {@link Statement}
 */
public class Executor {
	/**
	 * {@code execute()} will add edges to input and output ports of the given
	 * {@link Clause}, in case there wasn't one. According to the design principles of
	 * the node graph structure, edges are meant to be set before any execution, and
	 * changing edges between executions can be bug-full and may cause the program to
	 * crash. Therefore, edges are added only when there wasn't one.
	 *
	 * @param clause
	 * 		The {@link Clause} to execute
	 * @param input
	 * 		Input to the {@link Clause}
	 * @param <I>
	 * 		Input class
	 * @param <O>
	 * 		Output class
	 *
	 * @return Execution result of the {@link Clause}
	 */
	public static <I, O> O execute(Clause<I, O> clause, I input) {
		if (clause.getInput().getOutEdge() == null)
			new Edge<>(null, clause.getInput());
		if (clause.getOutput().getInEdge() == null)
			new Edge<>(clause.getOutput(), null);
		clause.getInput().getOutEdge().setValue(input);
		clause.execute();
		return clause.getOutput().getInEdge().getValue();
	}

	/**
	 * This method is not recommended to use, as it wraps the given {@link Statement}
	 * with a {@link Clause} whose reference is not returned. If further modification
	 * occurs on the {@link Statement}, errors may occur, therefore this method should
	 * be called with a {@link Statement} as input only when the {@link Statement}
	 * wouldn't be changed in any case.<br />
	 * ( Threotically interface {@link IProcessorLike} should be implemented by an
	 * subclass of {@link Statement}, though there is no technical way to ensure this )
	 *
	 * @param statement
	 * 		The {@link Statement} to execute
	 * @param input
	 * 		Input to the {@link Statement}
	 * @param <I>
	 * 		Input class
	 * @param <O>
	 * 		Output class
	 *
	 * @return Execution result of the {@link Statement}.
	 */
	public static <I, O> O execute(IProcessorLike<I, O> statement, I input) {
		Clause<I, O> clause = new Clause<>(statement);
		return execute(clause, input);
	}
}
