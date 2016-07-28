package terraingen.test;

import terraingen.backend.nodegraph.Executor;
import terraingen.backend.nodegraph.WhileClause;

import static terraingen.backend.nodegraph.NodeGraphHelper.create;
import static terraingen.backend.nodegraph.NodeGraphHelper.embraceStatement;

/**
 *
 */
public class WhileClauseTest {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// should be "cadabra"
		System.out.println((String) Executor.execute(new WhileClause<>(
						embraceStatement(create(
								(String str) -> !str.toLowerCase().startsWith("c"))),
						embraceStatement(create((String str) -> str.substring(1)))),
				"abracadabra"));
	}
}
