package terraingen.backend.nodegraph;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
	 * Connects the nodes together
	 *
	 * @param nodes
	 * 		nodes
	 */
	@SuppressWarnings("unchecked")
	public static void connect(Node... nodes) {
		try {
			int size = nodes.length - 1;
			for (int i = 0; i < size; ++i) {
				Node node = nodes[i], node1 = nodes[i + 1];
				if (node.getOutputs().size() > 0 && node1.getInputs().size() > 0)
					new Edge<>((OutputPort) node.getOutputs().get(0),
							(InputPort) node1.getInputs().get(0));
			}
		} catch (ClassCastException e) {
			log.error("Input and output classes don't match.", e);
		} catch (Exception e) {
			log.error("Exception caught.", e);
		}
	}

	@SuppressWarnings("unchecked")
	public static Statement<?, ?> embrace(Node... nodes) {
		connect(nodes);
		return new Statement((InputPort) nodes[0].getInputs().get(0),
				(OutputPort) nodes[nodes.length - 1].getOutputs().get(0));
	}

	@SuppressWarnings("unchecked")
	public static SupplierNode<?> embrace(SupplierNode<?> supplier,
										  Node... nodes) {
		return create(new CompositeSupplier(supplier, embrace(nodes)));
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// a extremely simple way to create node graphs
		System.out.println(Executor.execute((SupplierNode<Integer>) embrace(
				create(() -> 2),
				create((Integer i) -> i * 3),
				create((Integer i) -> i + 2),
				create((Integer i) -> i / 4)
		)));
	}
}
