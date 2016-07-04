package terraingen.backend.graghlike;

/**
 * Interface for statements that act like processors: have one input, provide one output.
 */
public interface IProcessorLike<I, O> {
	public IInput<I> getInput();

	public IOutput<O> getOutput();
}
