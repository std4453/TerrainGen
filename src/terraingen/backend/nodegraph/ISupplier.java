package terraingen.backend.nodegraph;

/**
 * Supplies tail of class O
 */
public interface ISupplier<O> extends IComponent {
	public O supply();
}
