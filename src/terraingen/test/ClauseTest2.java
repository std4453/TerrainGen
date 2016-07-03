package terraingen.test;

import terraingen.backend.graghlike.*;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class ClauseTest2 {
	public static void main(String[] args) {
		DisassemblerStatement<Integer[], Integer> s1 = new DisassemblerStatement<>(
				new IDisassembler<Integer[], Integer>() {
					@Override
					public List<Integer> disassemble(Integer[] input) {
						return Arrays.asList(input);
					}
				}, 2);
		DisassemblerStatement<Integer, Integer> s2 = new DisassemblerStatement<>(
				new IDisassembler<Integer, Integer>() {
					@Override
					public List<Integer> disassemble(Integer input) {
						return Arrays.asList(new Integer[]{input, input + 1});
					}
				}, 2);
		CombinerStatement<Integer, Integer> s3 = new CombinerStatement<>(
				new ICombiner<Integer, Integer>() {
					@Override
					public Integer combine(Integer input1, Integer input2) {
						return input1 * input2;
					}
				});
		CombinerStatement<Integer, Integer> s4 = new CombinerStatement<>(
				new ICombiner<Integer, Integer>() {
					@Override
					public Integer combine(Integer input1, Integer input2) {
						return input1 * input2;
					}
				});

		new Edge<>(s1.getOutput(0), s3.getInput1());
		new Edge<>(s1.getOutput(1), s2.getInput());
		new Edge<>(s2.getOutput(0), s3.getInput2());
		new Edge<>(s2.getOutput(1), s4.getInput2());
		new Edge<>(s3.getOutput(), s4.getInput1());

		Clause<Integer[], Integer> clause = new Clause<>(s1.getInput(), s4.getOutput());

		Integer ans = Executor.execute(clause, new Integer[]{2, 3});
		System.out.println(ans);   // should be 24
	}
}