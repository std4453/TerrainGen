package terraingen.backend.nodegraph;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Like the C / Java switch clause, the {@code SwitchClause} accepts a mapper that maps
 * the input to a key, which is used to determine which route ( {@link Statement} ) is
 * executed.
 */
public class SwitchClause<K, I, O> extends Node<I, O> implements IProcessorLike<I, O> {
	private static Log log = LogFactory.getLog(SwitchClause.class);

	protected Statement<I, K> mapper;
	protected Map<K, Statement<I, O>> map;
	protected Map<K, Edge<I>> headsMap;
	protected Map<K, Edge<O>> tailsMap;
	protected Edge<I> mapperHead;
	protected Edge<K> mapperTail;

	protected InputPort<I> input;
	protected OutputPort<O> output;

	public SwitchClause(Statement<I, K> mapper, Map<K, Statement<I, O>> map) {
		this.mapper = mapper;
		this.map = map;

		// bind half edges
		this.headsMap = new HashMap<>();
		this.tailsMap = new HashMap<>();
		this.mapperHead = new Edge<>(null, mapper.getInput());
		this.mapperTail = new Edge<>(mapper.getOutput(), null);
		for (Map.Entry<K, Statement<I, O>> entry : map.entrySet()) {
			Edge<I> headEdge = new Edge<>(null, entry.getValue().getInput());
			Edge<O> tailEdge = new Edge<>(entry.getValue().getOutput(), null);
			this.headsMap.put(entry.getKey(), headEdge);
			this.tailsMap.put(entry.getKey(), tailEdge);
		}

		// external input & output
		this.input = new InputPort<>(this);
		this.output = new OutputPort<>(this);
		this.inputCollection.add(this.input);
		this.outputCollection.add(this.output);
	}

	@Override
	public InputPort<I> getInput() {
		return this.input;
	}

	@Override
	public OutputPort<O> getOutput() {
		return this.output;
	}

	@Override
	public void execute() {
		super.execute();

		I input = this.input.getOutEdge().getValue();
		K key = Executor.execute(this.mapper, input);

		if (this.map.containsKey(key)) {
			O output = Executor.execute(this.map.get(key), input);
			this.output.getInEdge().setValue(output);
		} else {
			log.error("Mapped key don't have corresponding route.");
			this.output.getInEdge().setValue(null);
		}
	}
}
