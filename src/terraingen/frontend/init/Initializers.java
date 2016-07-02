package terraingen.frontend.init;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Loaded Initializers:<br />
 * <table>
 * <tr><td>Name</td><td>Class</td><td>Description</td></tr>
 * <tr><td>ui</td><td>{@link terraingen.frontend.init.InitUI InitUI}</td><td>User interface module</td></tr>
 * </table>
 */
public class Initializers {
	private static Log log = LogFactory.getLog(Initializers.class);
	private static Map<String, IInitializer> registry = new HashMap<>();

	static {
		register("ui", new InitUI());
		// TODO: More initializers
	}

	private static void register(String name, IInitializer initializer) {
		registry.put(name, initializer);
		log.info("Initializer \"" + name + "\" registered.");
	}

	public static boolean hasInitializer(String name) {
		return registry.containsKey(name);
	}

	public static IInitializer getInitializer(String name) {
		return registry.get(name);
	}
}
