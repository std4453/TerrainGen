package terraingen.backend.graghlike;

/**
 * Processes head of class I and outputs an tail of class O
 */
public interface IProcessor<I, O> extends IComponent {
	public O process(I input);
}
