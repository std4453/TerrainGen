package terraingen.test;

import terraingen.backend.nodegraph.*;

import java.util.Arrays;

import static terraingen.backend.nodegraph.NodeGraphHelper.connect;
import static terraingen.backend.nodegraph.NodeGraphHelper.create;

/**
 *
 */
public class ClauseTest1 {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		DisassemblerNode<Integer[], Integer> s1 = create(
				(IDisassembler<Integer[], Integer>) Arrays::asList, 3);
		CombinerNode<Integer, Integer> s2 = create(Integer::sum);
		MultiCombinerNode<Integer, Integer> s3 = create(
				(IMultiCombiner<Integer, Integer>) (input) -> input.stream().
						reduce(1, (a, b) -> a * b), 3);
		CombinerNode<Integer, Integer> s7 = create((a, b) -> a - b);

		connect(s1, s2, s7.getInput1(),
				s1.getOutput(1), s2.getInput2(),
				s1.getOutput(2), s3, s7.getInput2(),
				create(() -> 2), s3.getInput(1),
				create(() -> 1), create((Integer n) -> -n), s3.getInput(2));

		System.out.println(Executor.execute(new Statement<>(s1.getInput(),
				s7.getOutput()), new Integer[]{1, 2, 3}));
	}
}
