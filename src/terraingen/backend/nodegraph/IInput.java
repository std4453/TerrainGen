package terraingen.backend.nodegraph;

/**
 * Input interface of {@link Statement}
 */
public class IInput<I> {
	protected final Statement<I, ?> parent;
	protected Edge<I> outEdge;

	public IInput(Statement<I, ?> parent) {
		this.parent = parent;
	}

	public Statement<I, ?> getParent() {
		return this.parent;
	}

	public Edge<I> getOutEdge() {
		return this.outEdge;
	}

	public void setOutEdge(Edge<I> outEdge) {
		this.outEdge = outEdge;
	}
}
