package terraingen.backend.nodegraph;

/**
 * Input interface of {@link Node}
 */
public class InputPort<I> {
	protected final Node<I, ?> parent;
	protected Edge<I> outEdge;

	/**
	 * Automatically add port to input collection
	 *
	 * @param parent
	 * 		Parent {@link Node}
	 */
	public InputPort(Node<I, ?> parent) {
		this.parent = parent;
		if (parent != null)
			parent.addInput(this);
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
