package terraingen.test;

import terraingen.backend.nodegraph.Executor;
import terraingen.backend.nodegraph.RepeatClause;

import static terraingen.backend.nodegraph.NodeGraphHelper.create;
import static terraingen.backend.nodegraph.NodeGraphHelper.embraceStatement;

/**
 *
 */
public class RepeatClauseTest {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		System.out.println((Integer) Executor.execute(new RepeatClause<>(8,
				embraceStatement(create((Integer n) -> n * 2))), 1));    // 256
	}
}
