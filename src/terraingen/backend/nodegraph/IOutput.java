package terraingen.backend.nodegraph;

/**
 * Output interface of {@link Node}
 */
public class IOutput<O> {
	protected final Node<?, O> parent;
	protected Edge<O> inEdge;

	public IOutput(Node<?, O> parent) {
		this.parent = parent;
	}

	public Node<?, O> getParent() {
		return this.parent;
	}

	public Edge<O> getInEdge() {
		return this.inEdge;
	}

	public void setInEdge(Edge<O> inEdge) {
		this.inEdge = inEdge;
	}
}
