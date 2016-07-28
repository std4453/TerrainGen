package terraingen.test;

import terraingen.backend.nodegraph.Executor;
import terraingen.backend.nodegraph.IfClause;

import static terraingen.backend.nodegraph.NodeGraphHelper.create;
import static terraingen.backend.nodegraph.NodeGraphHelper.embraceStatement;

/**
 *
 */
public class IfClauseTest {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		IfClause<Integer, String> ifClause = new IfClause<>(
				embraceStatement(create((Integer n) -> n % 2 == 0)),
				embraceStatement(create(
						(str) -> String.format("%s is an even number", str))),
				embraceStatement(create(
						(str) -> String.format("%s is ann odd number", str))));

		for (int i = 1; i <= 10; ++i) {
			System.out.println(String.format("Input number %d:", i));
			System.out.println(Executor.execute(ifClause, i));
		}
	}
}
