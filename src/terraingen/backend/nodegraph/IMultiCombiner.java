package terraingen.backend.nodegraph;

import java.util.List;

/**
 * Combines multiple head of class I to an tail of class O
 */
public interface IMultiCombiner<I, O> extends IComponent {
	O combine(List<I> inputs);
}
