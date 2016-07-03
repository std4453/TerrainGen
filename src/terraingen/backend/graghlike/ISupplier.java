package terraingen.backend.graghlike;

/**
 * Supplies tail of class O
 */
public interface ISupplier<O> extends IComponent {
	public O supply();
}
