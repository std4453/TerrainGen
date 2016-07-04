package terraingen.backend.nodegraph;

/**
 * Combines two input of class I to an output of class O
 */
public interface ICombiner<I, O> extends IComponent {
	O combine(I input1, I input2);
}
