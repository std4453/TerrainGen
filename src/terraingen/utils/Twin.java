package terraingen.utils;

/**
 *
 */
public class Twin<A> extends Pair<A, A> {
	public Twin(A a, A a2) {
		super(a, a2);
	}

	/**
	 * Simulate a simple list-like operation
	 *
	 * @param a
	 * 		A
	 */
	public void add(A a) {
		if (this.a == null)
			this.a = a;
		else if (this.b == null)
			this.b = a;
	}
}
