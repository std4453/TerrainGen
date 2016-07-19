package terraingen.test;

import terraingen.backend.nodegraph.*;

/**
 *
 */
public class RepeatClauseTest {
	public static void main(String[] args) {
		RepeatClause<Integer> repeatClause = new RepeatClause<>(8,
				new Statement<>(new ProcessorNode<>(new IProcessor<Integer, Integer>() {
					@Override
					public Integer process(Integer input) {
						return input * 2;
					}
				})));
		System.out.println(Executor.execute(repeatClause, 1));    // 256
	}
}
