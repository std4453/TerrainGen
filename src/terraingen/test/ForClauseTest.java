package terraingen.test;

import terraingen.backend.nodegraph.*;

/**
 *
 */
public class ForClauseTest {
	public static void main(String[] args) {
		SupplierNode<Integer> initializer = new SupplierNode<>(
				new ISupplier<Integer>() {
					@Override
					public Integer supply() {
						return 0;
					}
				});
		ProcessorNode<Integer, Boolean> interrupter = new ProcessorNode<>(
				new IProcessor<Integer, Boolean>() {
					@Override
					public Boolean process(Integer input) {
						return input < 10;
					}
				});
		ProcessorNode<Integer, Integer> iteration = new ProcessorNode<>(
				new IProcessor<Integer, Integer>() {
					@Override
					public Integer process(Integer input) {
						return input + 1;
					}
				});
		ProcessorNode<String, String> body = new ProcessorNode<>(
				new IProcessor<String, String>() {
					@Override
					public String process(String input) {
						return input + "Hello.\n";
					}
				});

		ForClause<Integer, String> forClause = new ForClause<>(initializer,
				new Statement<>
						(interrupter), new Statement<>(iteration), new Statement<>(body));
		System.out.println(Executor.execute(forClause, ""));   // should be 10 "Hello."
	}
}
