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
	 * Executes the given node using constructor {@link Statement}{@code (Node)}.<br />
	 * Using this method may be risky, you should make sure the node has exactly one
	 * input and output port, or an exception may be thrown.
	 *
	 * @param node
	 * 		node to execute
	 * @param input
	 * 		input of node
	 * @param <I>
	 * 		Input class
	 * @param <O>
	 * 		Output class
	 *
	 * @return Result of execution
	 */
	public static <I, O> O execute(Node<I, O> node, I input) {
		return execute(new Statement<>(node), input);
	}

	/**
	 * Executes the given {@link SupplierNode} and returns result.
	 *
	 * @param supplier
	 * 		The {@link SupplierNode}
	 * @param <V>
	 * 		Supplier class
	 *
	 * @return Result of supplier
	 */
	public static <V> V execute(SupplierNode<V> supplier) {
		if (supplier.getOutput().getInEdge() == null)
			new Edge<>(supplier.getOutput(), null);
		supplier.execute();
		return supplier.getOutput().getInEdge().getValue();
	}
}
