package terraingen.backend.nodegraph;

/**
 * Input interface of {@link Node}
 */
public class IInput<I> {
	protected final Node<I, ?> parent;
	protected Edge<I> outEdge;

	public IInput(Node<I, ?> parent) {
		this.parent = parent;
	}

	public Node<I, ?> getParent() {
		return this.parent;
	}

	public Edge<I> getOutEdge() {
		return this.outEdge;
	}

	public void setOutEdge(Edge<I> outEdge) {
		this.outEdge = outEdge;
	}
}
