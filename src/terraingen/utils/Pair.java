package terraingen.utils;

/**
 *
 */
public class Pair<A, B> {
	public A a;
	public B b;

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	public Pair() {
		this(null, null);
	}

	public A getA() {
		return this.a;
	}

	public void setA(A a) {
		this.a = a;
	}

	public B getB() {
		return this.b;
	}

	public void setB(B b) {
		this.b = b;
	}

	public void set(Pair<A, B> pair) {
		this.set(pair.a, pair.b);
	}

	public void set(A a, B b) {
		this.a = a;
		this.b = b;
	}

	/**
	 * Auto-generated {@code equals()}
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Pair<?, ?> pair = (Pair<?, ?>) o;

		return this.a != null ? this.a.equals(
				pair.a) : pair.a == null && (this.b != null ? this.b.equals(
				pair.b) : pair.b == null);

	}
}
