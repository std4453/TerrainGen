package terraingen.backend.nodegraph;

/**
 * Supplies output of class O
 */
public interface ISupplier<O> extends IComponent {
	O supply();
}
