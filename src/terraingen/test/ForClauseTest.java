package terraingen.test;

import terraingen.backend.nodegraph.Executor;
import terraingen.backend.nodegraph.ForClause;
import terraingen.backend.nodegraph.Statement;

import static terraingen.backend.nodegraph.NodeGraphHelper.create;

/**
 *
 */
public class ForClauseTest {
	public static void main(String[] args) {
		System.out.println(Executor.execute(new ForClause<>(create(() -> 0),
				new Statement<Integer, Boolean>(create((n) -> (n < 10))),
				new Statement<>(create((n) -> n + 1)),
				new Statement<>(create((str) -> str + "Hello.\n"))), ""));
	}
}
