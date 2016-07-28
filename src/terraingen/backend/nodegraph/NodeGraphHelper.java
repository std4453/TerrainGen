package terraingen.backend.nodegraph;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import terraingen.utils.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class NodeGraphHelper {
	private static final Log log = LogFactory.getLog(NodeGraphHelper.class);

	public static <I, O> CombinerNode<I, O> create(ICombiner<I, O> combiner) {
		return new CombinerNode<>(combiner);
	}

	public static <V> ConsumerNode<V> create(IConsumer<V> consumer) {
		return new ConsumerNode<>(consumer);
	}

	public static <V> SupplierNode<V> create(ISupplier<V> supplier) {
		return new SupplierNode<>(supplier);
	}

	public static <I, O> DisassemblerNode<I, O> create(IDisassembler<I, O> disassembler,
													   int outputCount) {
		return new DisassemblerNode<>(disassembler, outputCount);
	}

	public static <I, O> MultiCombinerNode<I, O> create(IMultiCombiner<I, O>
																multiCombiner, int
																outputCount) {
		return new MultiCombinerNode<>(multiCombiner, outputCount);
	}

	public static <I, O> ProcessorNode<I, O> create(IProcessor<I, O> processor) {
		return new ProcessorNode<>(processor);
	}

	/**
	 * Intelligent method of processors / input ports / output ports / suppliers /
	 * consumers connecting.
	 */
	@SuppressWarnings("unchecked")
	public static void connect(Object... nodes) {
		if (nodes.length == 0)
			return;
		List<Object> nodesList = Arrays.asList(nodes);
		((Pair<ArrayList<Pair<OutputPort, InputPort>>, ?>) ((ArrayList)
				nodesList.stream().filter((obj) -> obj != null)
						.filter((obj) -> isInput(obj) || isOutput(obj))
						.reduce(
								new ArrayList<>(), (list, obj) -> {
									if (isInput(obj))
										((List) list).add(getInput(obj));
									if (isOutput(obj))
										((List) list).add(getOutput(obj));
									return list;
								}
						)).stream().reduce(
				new Pair<ArrayList<Pair<OutputPort, InputPort>>, Pair<
						OutputPort, InputPort>>(new ArrayList<>(), null),
				(_pair, obj) -> {
					Pair<ArrayList<Pair<OutputPort, InputPort>>,
							Pair<OutputPort, InputPort>> pair = (Pair) _pair;
					if (isOutput(obj) && pair.b == null)
						pair.setB(new Pair<>(getOutput(obj), null));
					else if (isInput(obj) && pair.b != null && pair.b.b == null) {
						pair.b.setB(getInput(obj));
						pair.a.add(pair.b);
						pair.setB(null);
					}
					return pair;
				}
		)).a.forEach((pair) -> new Edge<>(pair.a, pair.b));
	}

	@SuppressWarnings("unchecked")
	public static Object embrace(Object... nodes) {
		if (nodes.length == 0) {
			log.warn("At least one node / IO port required");
		}

		Object head = nodes[0], tail = nodes[nodes.length - 1];

		if (isInput(head) && isOutput(tail)) {
			connect(nodes);
			try {
				return new Statement(getInput(head), getOutput(tail));
			} catch (Exception e) {
				log.error("Statement build error: nodes internally not connectible", e);
				return null;
			}
		}
		if (isOutput(tail)) {
			if (nodes.length < 2 || !isInput(nodes[1]) ||
					!(head instanceof SupplierNode)) {
				log.error("Nodes internally not connectible.");
				return null;
			}
			Object nodes1[] = new Object[nodes.length - 1];
			System.arraycopy(nodes, 1, nodes1, 0, nodes1.length);
			connect(nodes1);
			return new SupplierNode(new CompositeSupplier((SupplierNode) head,
					new Statement(getInput(nodes[1]), getOutput(tail))));
		}
		if (isInput(head)) {
			if (nodes.length < 2 && !isOutput(nodes[nodes.length - 2]) ||
					!(tail instanceof ConsumerNode)) {
				log.error("Nodes internally not connectible.");
				return null;
			}
			Object nodes1[] = new Object[nodes.length - 1];
			System.arraycopy(nodes, 0, nodes1, 0, nodes1.length);
			connect(nodes1);
			return new ConsumerNode(new CompositeConsumer((ConsumerNode) tail,
					new Statement(getInput(head), getOutput(nodes[nodes.length - 2]))));
		}
		return null;
	}

	private static boolean isInput(Object obj) {
		return obj != null && (obj instanceof InputPort || (obj instanceof Node &&
				((Node) obj).getInputs().size() > 0));
	}

	private static boolean isOutput(Object obj) {
		return obj != null && (obj instanceof OutputPort || (obj instanceof Node &&
				((Node) obj).getOutputs().size() > 0));
	}

	private static InputPort getInput(Object obj) {
		return isInput(obj) ? (obj instanceof InputPort ? (InputPort) obj :
				((Node<?, ?>) obj).getInputs().get(0)) : null;
	}

	private static OutputPort getOutput(Object obj) {
		return isOutput(obj) ? (obj instanceof OutputPort ? (OutputPort) obj :
				((Node<?, ?>) obj).getOutputs().get(0)) : null;
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// a extremely simple way to create node graphs
		System.out.println(Executor.execute((SupplierNode<Integer>) embrace(
				create(() -> 2),
				create((Integer i) -> i * 3),
				create((Integer i) -> i + 2),
				create((Integer i) -> i * 4)
		)));
	}
}
