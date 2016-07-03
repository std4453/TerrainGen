package terraingen.backend.graghlike;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * A clause contains many {@link Statement}s and execute them.<br />
 * One execution process should follow the following rules:<br />
 * <ol>
 * <li>One statement can be executed only once</li>
 * <li>One statement is executed only after every {@link Statement} that serves
 * as its predecessor ( head ) is successfully executed</li>
 * <li>There is only one head and one tail ( while multiple head & tail can be
 * achieved using arrays or other collections )</li>
 * <li>No loop should be formed in a clause ( Loops are achieved using
 * {@link LoopStatement} )</li>
 * </ol>
 * Therefore anyone who makes a structure with {@code Clause} should be aware of these
 * rules, while no further check is done while the structure is being built.
 */
public class Clause<I, O> extends Statement<I, O> {
	private static Log log = LogFactory.getLog(Clause.class);

	protected IInput<I> input;
	protected IOutput<O> output;

	protected BlankStatement<I> head;
	protected Edge<I> headHalfEdge;
	protected BlankStatement<O> tail;
	protected Edge<O> tailHalfEdge;

	protected List<SupplierStatement> suppliers;
	protected List<Edge> edges;
	protected List<Statement> statements;

	public Clause(IInput<I> contentInput, IOutput<O> contentOutput) {
		// input & output for external access
		this.input = new IInput<>(this);
		this.output = new IOutput<>(this);
		this.inputCollection.add(this.input);
		this.outputCollection.add(this.output);

		// input & output for internal access
		this.head = new BlankStatement<>();
		this.tail = new BlankStatement<>();
		// half-edges to pass data between inside and outside
		this.headHalfEdge = new Edge<>(null, this.head.getInputs().get(0));
		this.tailHalfEdge = new Edge<>(this.tail.getOutputs().get(0), null);
		new Edge<>(this.head.getOutputs().get(0), contentInput);
		new Edge<>(contentOutput, this.tail.getInputs().get(0));

		// caches
		this.suppliers = new ArrayList<>();
		this.edges = new ArrayList<>();
		this.statements = new ArrayList<>();
		buildCaches();
	}

	protected void buildCaches() {
		Queue<Statement> queue = new ArrayDeque<>();
		queue.offer(this.tail);
		this.statements.add(this.tail);
		while (!queue.isEmpty()) {
			Statement statement = queue.poll();

			if (statement instanceof SupplierStatement)
				this.suppliers.add((SupplierStatement) statement);

			if (statement != this.head) {
				List<IInput<?>> inputs = statement.getInputs();
				for (IInput input : inputs) {
					this.edges.add(input.getOutEdge());
					Statement ancestor = input.getOutEdge().getOutput().getParent();
					if (!this.statements.contains(ancestor)) {
						queue.offer(ancestor);
						this.statements.add(ancestor);
					}
				}
			}
		}
	}
	
	/**
	 * Calls {@code clearValue()} of every edge of statements in this clause and
	 * {@code clearExecuted()} of every statement ( except for {@code this.head} &
	 * {@code this.tail} )
	 */
	protected void clear() {
		for (Edge edge : this.edges)
			edge.clearValue();
		for (Statement statement : this.statements)
			statement.clearExecuted();
	}

	/**
	 * Is every head of this statement provided with data?
	 *
	 * @param statement
	 * 		The statement to check
	 *
	 * @return Whether the statement ready for execution
	 */
	protected boolean isStatementReady(Statement statement) {
		boolean statementReady = true;
		List<IInput<?>> inputList = statement.getInputs();
		for (IInput<?> input : inputList)
			if (input.getOutEdge() != null && input.getOutEdge().isValueSet()) {
				statementReady = false;
				break;
			}
		
		return statementReady;
	}
	
	@Override
	public void execute() {
		super.execute();

		// copy input value
		I input = this.input.getOutEdge().getValue();
		this.headHalfEdge.setValue(input);

		// pre-execution
		clear();

		// execute using BFS
		Queue<Statement> queue = new ArrayDeque<>();
		queue.offer(this.head);
		for (SupplierStatement supplier : this.suppliers)
			queue.offer(supplier);
		Statement statement;
		while (!queue.isEmpty()) {
			statement = queue.poll();
			System.out.println(statement.getClass().getName());

			// check whether the statement is ready for execution
			if (statement == null) {
				log.warn("Non-null statement.");
				continue;
			}
			if (statement.isExecuted() || !isStatementReady(statement))
				continue;

			// execute it!
			statement.execute();

			// add its descendants to the queue, if, it is not the final one
			if (statement != this.tail) {
				List<IOutput<?>> outputs = statement.getOutputs();
				for (IOutput<?> output : outputs) {
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

	public IInput<I> getInput() {
		return this.input;
	}

	public IOutput<O> getOutput() {
		return this.output;
	}
}
