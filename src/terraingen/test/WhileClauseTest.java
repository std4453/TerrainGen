package terraingen.test;

import terraingen.backend.nodegraph.*;

/**
 *
 */
public class WhileClauseTest {
	public static void main(String[] args) {
		ProcessorNode<String, Boolean> startsWithC = new ProcessorNode<>(
				new IProcessor<String, Boolean>() {
					@Override
					public Boolean process(String input) {
						return !input.toLowerCase().startsWith("c");
					}
				});
		ProcessorNode<String, String> iterator = new ProcessorNode<>(
				new IProcessor<String, String>() {
					@Override
					public String process(String input) {
						return input.substring(1);
					}
				});

		WhileClause<String> whileClause = new WhileClause<>(new Statement<>(startsWithC),
				new Statement<>(iterator));
		// should be "cadabra"
		System.out.println(Executor.execute(whileClause, "abracadabra"));
	}
}
