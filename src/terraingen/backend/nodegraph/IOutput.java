package terraingen.backend.nodegraph;

/**
 * Output interface of {@link Statement}
 */
public class IOutput<O> {
	protected final Statement<?, O> parent;
	protected Edge<O> inEdge;

	public IOutput(Statement<?, O> parent) {
		this.parent = parent;
	}

	public Statement<?, O> getParent() {
		return this.parent;
	}

	public Edge<O> getInEdge() {
		return this.inEdge;
	}

	public void setInEdge(Edge<O> inEdge) {
		this.inEdge = inEdge;
	}
}
