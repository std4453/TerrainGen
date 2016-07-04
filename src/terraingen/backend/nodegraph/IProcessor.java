package terraingen.backend.nodegraph;

/**
 * Processes input of class I and outputs an output of class O
 */
public interface IProcessor<I, O> extends IComponent {
	O process(I input);
}
