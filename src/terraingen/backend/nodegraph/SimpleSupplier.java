package terraingen.backend.nodegraph;

/**
 *
 */
public class SimpleSupplier<V> implements ISupplier<V> {
	protected V v;

	public SimpleSupplier(V v) {
		this.v = v;
	}

	@Override
	public V supply() {
		return this.v;
	}
}
