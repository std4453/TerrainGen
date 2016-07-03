package terraingen.backend.graghlike;

import java.util.List;

/**
 * Combines multiple head of class I to an tail of class O
 */
public interface IMultiCombiner<I, O> extends IComponent {
	public O combine(List<I> inputs);
}
