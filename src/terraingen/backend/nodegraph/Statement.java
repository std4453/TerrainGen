package terraingen.backend.nodegraph;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * A statement contains many {@link Node}s and execute them.<br />
 * One execution process should follow the following rules:<br />
 * <ol>
 * <li>One node can be executed only once</li>
 * <li>One node is executed only after every {@link Node} that serves as its
 * predecessor ( input ) is successfully executed</li>
 * <li>There is only one input and one output ( while multiple input & output can be
 * achieved using arrays or other collections )</li>
 * <li>No loop should be formed in a statement ( Loops are achieved using
 * {@link ForClause} )</li>
 * </ol>
 * Therefore anyone who makes a structure with {@code Statement} should be aware of these
 * rules, while no further check is done while the structure is being built.
 */
public class Statement<I, O> extends Node<I, O> implements IProcessorLike<I, O> {
	private static Log log = LogFactory.getLog(Statement.class);

	protected InputPort<I> input;
	protected OutputPort<O> output;

	protected BlankNode<I> head;
	protected Edge<I> headHalfEdge;
	protected BlankNode<O> tail;
	protected Edge<O> tailHalfEdge;

	protected List<SupplierNode> suppliers;
	protected List<Edge> edges;
	protected List<Node> nodes;

	public Statement(InputPort<I> contentInput, OutputPort<O> contentOutput) {
		// input & output for external access
		this.input = new InputPort<>(this);
		this.output = new OutputPort<>(this);
		this.inputCollection.add(this.input);
		this.outputCollection.add(this.output);

		// input & output for internal access
		this.head = new BlankNode<>();
		this.tail = new BlankNode<>();
		// half-edges to pass data between inside and outside
		this.headHalfEdge = new Edge<>(null, this.head.getInputs().get(0));
		this.tailHalfEdge = new Edge<>(this.tail.getOutputs().get(0), null);
		new Edge<>(this.head.getOutputs().get(0), contentInput);
		new Edge<>(contentOutput, this.tail.getInputs().get(0));

		// caches
		this.suppliers = new ArrayList<>();
		this.edges = new ArrayList<>();
		this.nodes = new ArrayList<>();
		buildCaches();
	}

	public Statement(OutputPort<O> contentOutput, InputPort<I> contentInput) {
		this(contentInput, contentOutput);
	}

	public Statement(IProcessorLike<I, O> statement) {
		this(statement.getInput(), statement.getOutput());
	}

	/**
	 * Convenient constructor to generate a {@code Statement} wrapping a given node, uses
	 * first input and output port of the given node.<br />
	 * An exception will be thrown if node has no input or output.
	 *
	 * @param node
	 * 		Node around which to wrap {@code Statement}
	 */
	public Statement(Node<I, O> node) {
		this(node.getInputs().get(0), node.getOutputs().get(0));
	}

	protected void buildCaches() {
		Queue<Node> queue = new ArrayDeque<>();
		queue.offer(this.tail);
		this.nodes.add(this.tail);
		while (!queue.isEmpty()) {
			Node node = queue.poll();

			if (node instanceof SupplierNode)
				this.suppliers.add((SupplierNode) node);

			if (node != this.head) {
				List inputs = node.getInputs();
				for (Object inputObj : inputs) {
					if (!(inputObj instanceof InputPort))
						continue;
					InputPort<?> input = (InputPort) inputObj;
					this.edges.add(input.getOutEdge());
					Node ancestor = input.getOutEdge().getOutput().getParent();
					if (!this.nodes.contains(ancestor)) {
						queue.offer(ancestor);
						this.nodes.add(ancestor);
					}
				}
			}
		}
	}

	/**
	 * Calls {@code clearValue()} of every edge of nodes in this statement and
	 * {@code clearExecuted()} of every statement ( except for {@code this.head} &
	 * {@code this.tail} )
	 */
	protected void clear() {
		for (Edge edge : this.edges)
			edge.clearValue();
		for (Node node : this.nodes)
			node.clearExecuted();
	}

	/**
	 * Is every input of this node provided with data?
	 *
	 * @param node
	 * 		The node to check
	 *
	 * @return Whether the node ready for execution
	 */
	protected boolean isNodeReady(Node node) {
		boolean nodeReady = true;
		List inputList = node.getInputs();
		for (Object inputObj : inputList) {
			if (!(inputObj instanceof InputPort))
				continue;
			InputPort<?> input = (InputPort) inputObj;
			if (input.getOutEdge() == null || !input.getOutEdge().isValueSet()) {
				nodeReady = false;
				break;
			}
		}

		return nodeReady;
	}

	@Override
	public void execute() {
		super.execute();

		// pre-execution
		clear();

		// copy input value
		I input = this.input.getOutEdge().getValue();
		this.headHalfEdge.setValue(input);

		// execute using BFS
		Queue<Node> queue = new ArrayDeque<>();
		queue.offer(this.head);
		for (SupplierNode supplier : this.suppliers)
			queue.offer(supplier);
		Node node;
		while (!queue.isEmpty()) {
			node = queue.poll();

			// check whether the node is ready for execution
			if (node == null) {
				log.warn("Non-null node.");
				continue;
			}
			if (node.isExecuted() || !isNodeReady(node))
				continue;

			// execute it!
			node.execute();

			// add its descendants to the queue, if, it is not the final one
			if (node != this.tail) {
				List outputs = node.getOutputs();
				for (Object outputObj : outputs) {
					if (!(outputObj instanceof OutputPort))
						continue;
					OutputPort<?> output = (OutputPort) outputObj;
					if (output.getInEdge() == null) {
						log.warn("Output should have an edge.");
						continue;
					}
					queue.offer(output.getInEdge().getInput().getParent());
				}
			}
		}

		// copy output value
		O output = this.tailHalfEdge.getValue();
		this.output.getInEdge().setValue(output);
	}

	@Override
	public InputPort<I> getInput() {
		return this.input;
	}

	@Override
	public OutputPort<O> getOutput() {
		return this.output;
	}
}
