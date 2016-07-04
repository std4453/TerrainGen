package terraingen.test;

import terraingen.backend.nodegraph.*;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class SwitchClauseTest {
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

		IProcessor<Integer, String> processor = input -> String.format("%s is in %s.",
				months[input], seasons[input]);
		IProcessor<Integer, String> mapper = input -> seasons[input];
		Map<String, Statement<Integer, String>> routes = new HashMap<>();
		for (int i = 0; i < 12; i += 3)
			routes.put(seasons[i], new Statement<>(new ProcessorNode<>(processor)));
		SwitchClause<String, Integer, String> switchClause = new SwitchClause<>(new
				Statement<>(new ProcessorNode<>(mapper)), routes);
		Statement<Integer, String> switchClauseStatement = new Statement<>(
				(IProcessorLike<Integer, String>) switchClause);

		for (int i = 0; i < 12; ++i)
			System.out.println(Executor.execute(switchClauseStatement, i));
	}
}
