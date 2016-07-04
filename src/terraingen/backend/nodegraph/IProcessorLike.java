package terraingen.backend.nodegraph;

/**
 * Interface for clause-level nodes, like {@link IfClause}
 */
public interface IProcessorLike<I, O> {
	IInput<I> getInput();

	IOutput<O> getOutput();
}
