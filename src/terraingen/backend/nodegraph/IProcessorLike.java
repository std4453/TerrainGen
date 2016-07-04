package terraingen.backend.nodegraph;

/**
 * Interface for statements that act like processors: have one input, provide one output.
 */
public interface IProcessorLike<I, O> {
	IInput<I> getInput();

	IOutput<O> getOutput();
}
