package terraingen.backend.graghlike;

/**
 * Execute given {@link Clause}
 */
public class Executor {
	public static <I, O> O execute(Clause<I, O> clause, I input) {
		Edge<I> inputHalfEdge = new Edge<>(null, clause.getInput());
		Edge<O> outputHalfEdge = new Edge<>(clause.getOutput(), null);
		inputHalfEdge.setValue(input);
		clause.execute();
		return outputHalfEdge.getValue();
	}
}
