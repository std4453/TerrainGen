package terraingen.test;

import terraingen.backend.nodegraph.*;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class ClauseTest1 {
	public static void main(String[] args) {
		// create
		DisassemblerNode<Integer[], Integer> s1 = new DisassemblerNode<>(
				new IDisassembler<Integer[], Integer>() {
					@Override
					public List<Integer> disassemble(Integer[] input) {
						return Arrays.asList(input);
					}
				}, 3);
		CombinerNode<Integer, Integer> s2 = new CombinerNode<>(
				new ICombiner<Integer, Integer>() {
					@Override
					public Integer combine(Integer input1, Integer input2) {
						return input1 + input2;
					}
				});
		MultiCombinerNode<Integer, Integer> s3 = new MultiCombinerNode<>(
				new IMultiCombiner<Integer, Integer>() {
					@Override
					public Integer combine(List<Integer> inputs) {
						int s = 1;
						for (int n : inputs)
							s *= n;
						return s;
					}
				}, 3);
		SupplierNode<Integer> s4 = new SupplierNode<>(
				new ISupplier<Integer>() {
					@Override
					public Integer supply() {
						return 2;
					}
				});
		SupplierNode<Integer> s5 = new SupplierNode<>(
				new ISupplier<Integer>() {
					@Override
					public Integer supply() {
						return 1;
					}
				});
		ProcessorNode<Integer, Integer> s6 = new ProcessorNode<>(
				new IProcessor<Integer, Integer>() {
					@Override
					public Integer process(Integer input) {
						return -input;
					}
				});
		CombinerNode<Integer, Integer> s7 = new CombinerNode<>(
				new ICombiner<Integer, Integer>() {
					@Override
					public Integer combine(Integer input1, Integer input2) {
						return input1 - input2;
					}
				});

		// connect
		new Edge<>(s1.getOutput(0), s2.getInput1());
		new Edge<>(s1.getOutput(1), s2.getInput2());
		new Edge<>(s1.getOutput(2), s3.getInput(0));
		new Edge<>(s4.getOutput(), s3.getInput(1));
		new Edge<>(s5.getOutput(), s6.getInput());
		new Edge<>(s6.getOutput(), s3.getInput(2));
		new Edge<>(s3.getOutput(), s7.getInput2());
		new Edge<>(s2.getOutput(), s7.getInput1());

		// create clause
		Statement<Integer[], Integer> clause = new Statement<>(s1.getInput(),
				s7.getOutput());

		// calculate!
		Integer ans = Executor.execute(clause, new Integer[]{1, 2, 3});
		System.out.println(ans);  // should be 9
	}
}
