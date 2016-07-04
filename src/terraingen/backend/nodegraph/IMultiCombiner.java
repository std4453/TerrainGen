package terraingen.backend.nodegraph;

import java.util.List;

/**
 * Combines multiple input of class I to an output of class O
 */
public interface IMultiCombiner<I, O> extends IComponent {
	O combine(List<I> inputs);
}
