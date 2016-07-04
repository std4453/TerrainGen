package terraingen.backend.nodegraph;

/**
 * Execute given {@link Statement} / {@link Node}
 */
public class Executor {
	/**
	 * {@code execute()} will add edges to input and output ports of the given
	 * {@link Statement}, in case there wasn't one. According to the design principles of
	 * the node graph structure, edges are meant to be set before any execution, and
	 * changing edges between executions can be bug-full and may cause the program to
	 * crash. Therefore, edges are added only when there wasn't one.
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
	 * @return Execution result of the {@link Statement}
	 */
	public static <I, O> O execute(Statement<I, O> statement, I input) {
		if (statement.getInput().getOutEdge() == null)
			new Edge<>(null, statement.getInput());
		if (statement.getOutput().getInEdge() == null)
			new Edge<>(statement.getOutput(), null);

		statement.getInput().getOutEdge().setValue(input);
		statement.execute();

		return statement.getOutput().getInEdge().getValue();
	}

	/**
	 * This method is not recommended to use, as it wraps the given {@link Node}
	 * with a {@link Statement} whose reference is not returned. If further modification
	 * occurs on the {@link Node}, errors may occur, therefore this method should
	 * be called with a {@link Node} as input only when the {@link Node}
	 * wouldn't be changed in any case.<br />
	 * ( Theoretically interface {@link IProcessorLike} should be implemented by an
	 * subclass of {@link Node}, though there is no technical way to ensure this )
	 *
	 * @param node
	 * 		The {@link Node} to execute
	 * @param input
	 * 		Input to the {@link Node}
	 * @param <I>
	 * 		Input class
	 * @param <O>
	 * 		Output class
	 *
	 * @return Execution result of the {@link Node}.
	 */
	public static <I, O> O execute(IProcessorLike<I, O> node, I input) {
		Statement<I, O> statement = new Statement<>(node);
		return execute(statement, input);
	}
}
