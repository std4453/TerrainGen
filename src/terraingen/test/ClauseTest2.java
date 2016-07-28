package terraingen.test;

import terraingen.backend.nodegraph.*;

import java.util.Arrays;

import static terraingen.backend.nodegraph.NodeGraphHelper.connect;
import static terraingen.backend.nodegraph.NodeGraphHelper.create;

/**
 *
 */
public class ClauseTest2 {
	public static void main(String[] args) {
		DisassemblerNode<Integer[], Integer> s1 = create(
				(IDisassembler<Integer[], Integer>) Arrays::asList, 2);
		DisassemblerNode<Integer, Integer> s2 = create(
				(IDisassembler<Integer, Integer>) (n) -> Arrays.asList(n, n + 1), 2);
		CombinerNode<Integer, Integer> s3 = create((a, b) -> a * b);
		CombinerNode<Integer, Integer> s4 = create((a, b) -> a * b);

		connect(s1, s3, s4.getInput1(),
				s1.getOutput(1), s2, s3.getInput2(),
				s2.getOutput(1), s4.getInput2());

		System.out.println(Executor.execute(new Statement<>(s1.getInput(),
				s4.getOutput()), new Integer[]{2, 3}));
	}
}
