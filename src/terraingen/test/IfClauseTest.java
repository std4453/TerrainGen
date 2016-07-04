package terraingen.test;

import terraingen.backend.nodegraph.*;

/**
 *
 */
public class IfClauseTest {
	public static void main(String[] args) {
		ProcessorNode<Integer, Boolean> conditioner = new ProcessorNode<>(
				new IProcessor<Integer, Boolean>() {
					@Override
					public Boolean process(Integer input) {
						return input % 2 == 0;
					}
				});
		ProcessorNode<Integer, String> route1 = new ProcessorNode<>(
				new IProcessor<Integer, String>() {
					@Override
					public String process(Integer input) {
						return String.format("%s is an even number", input);
					}
				});
		ProcessorNode<Integer, String> route2 = new ProcessorNode<>(
				new IProcessor<Integer, String>() {
					@Override
					public String process(Integer input) {
						return String.format("%s is an odd number", input);
					}
				});
		IfClause<Integer, String> ifClause = new IfClause<>(
				new Clause<>(conditioner.getInput(), conditioner.getOutput()),
				new Clause<>(route1.getInput(), route1.getOutput()),
				new Clause<>(route2.getInput(), route2.getOutput()));
		for (int i = 1; i <= 10; ++i) {
			System.out.println(String.format("Input number %d:", i));
			String output = Executor.execute(ifClause, i);
			System.out.println(output);
		}
	}
}
