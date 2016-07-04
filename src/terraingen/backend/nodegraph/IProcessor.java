package terraingen.backend.nodegraph;

/**
 * Processes head of class I and outputs an tail of class O
 */
public interface IProcessor<I, O> extends IComponent {
	O process(I input);
}
