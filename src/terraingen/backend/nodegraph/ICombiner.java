package terraingen.backend.nodegraph;

/**
 * Combines head of class I to an tail of class O
 */
public interface ICombiner<I, O> extends IComponent {
	public O combine(I input1, I input2);
}
