package terraingen.backend.nodegraph;

/**
 * Output interface of {@link Node}
 */
public class OutputPort<O> {
	protected final Node<?, O> parent;
	protected Edge<O> inEdge;

	/**
	 * Automatically add port to output collection
	 *
	 * @param parent
	 * 		Parent {@link Node}
	 */
	public OutputPort(Node<?, O> parent) {
		this.parent = parent;
		if (parent != null)
			parent.addOutput(this);
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
