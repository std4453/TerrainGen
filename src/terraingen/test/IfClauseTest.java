package terraingen.test;

import terraingen.backend.nodegraph.*;

/**
 *
 */
public class IfClauseTest {
	public static void main(String[] args) {
		ProcessorStatement<Integer, Boolean> conditioner = new ProcessorStatement<>(
				new IProcessor<Integer, Boolean>() {
					@Override
					public Boolean process(Integer input) {
						return input % 2 == 0;
					}
				});
		ProcessorStatement<Integer, String> route1 = new ProcessorStatement<>(
				new IProcessor<Integer, String>() {
					@Override
					public String process(Integer input) {
						return String.format("%s is an even number", input);
					}
				});
		ProcessorStatement<Integer, String> route2 = new ProcessorStatement<>(
				new IProcessor<Integer, String>() {
					@Override
					public String process(Integer input) {
						return String.format("%s is an odd number", input);
					}
				});
		IfClause<Integer, String> ifClause = new IfClause<>(new Clause<>(conditioner), new
				Clause<>(route1), new Clause<>(route2));
		for (int i = 0; i < 10; ++i) {
			System.out.println(String.format("Input number %d:", i));
			String output = Executor.execute(ifClause, i);
			System.out.println(output);
		}
	}
}
