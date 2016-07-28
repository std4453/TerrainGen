package terraingen.test;

import terraingen.backend.nodegraph.Executor;
import terraingen.backend.nodegraph.Statement;
import terraingen.backend.nodegraph.SwitchClause;

import java.util.HashMap;
import java.util.Map;

import static terraingen.backend.nodegraph.NodeGraphHelper.create;
import static terraingen.backend.nodegraph.NodeGraphHelper.embraceStatement;

/**
 *
 */
public class SwitchClauseTest {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		final String[] months = {
				"January", "February", "March",
				"April", "May", "June",
				"July", "August", "September",
				"October", "November", "December",
		};
		final String[] seasons = {
				"Spring", "Spring", "Spring",
				"Summer", "Summer", "Summer",
				"Autumn", "Autumn", "Autumn",
				"Winter", "Winter", "Winter",
		};

		Map<String, Statement<Integer, String>> routes = new HashMap<>();
		for (int i = 0; i < 12; i += 3)
			routes.put(seasons[i], embraceStatement(create(
					(Integer input) -> String.format("%s is in %s.", months[input],
							seasons[input]))));
		SwitchClause<String, Integer, String> switchClause = new SwitchClause<>(
				embraceStatement(create((Integer input) -> seasons[input])), routes);
		Statement<Integer, String> switchClauseStatement = new Statement<>(switchClause);

		for (int i = 0; i < 12; ++i)
			System.out.println(Executor.execute(switchClauseStatement, i));
	}
}
