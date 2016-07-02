package terraingen.frontend.init;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import terraingen.utils.IterableWrapper;

/**
 * Initializes the whole program, wherever the entry is, calling
 * {@code Initialization.load(<a configuration file>)} should be considered
 * necessary. <br />
 * <br />
 * Configuration files are loaded block by block, i.e., by considering every
 * children of the root of {@code conf.getRoot()}. A standard block should look
 * like this:
 * <p>
 * <code><pre>
 * "ui": {
 *     "style": "windows",
 *     "location": "center",
 * }
 * </pre></code>
 * <p>
 * And class {@code terraingen.frontend.init.InitUI.init()} is called with the
 * object passed, while the UI subsystem initializes itself in the meantime. <br />
 * Other blocks work in the same way. The initializer looks for corresponding
 * objects for the name of the block, then calls {@code init()} of the object.<br />
 * <br />
 * For detailed description of each block, find block by name in
 * {@link terraingen.frontend.init.Initializers Initializers}
 */
public class Initialization {
	private static Log log = LogFactory.getLog(Initialization.class);
	public static boolean initialized = false;

	/**
	 * A program should use only ONE initialization configuration file, so this
	 * method can be called only once in one lifecycle. For more information,
	 * see the Javadoc of this class.
	 */
	public static void load(Configuration conf) {
		JSONObject root = conf.getRoot();
		for (String key : new IterableWrapper<>(root.keys()))
			if (root.has(key) && Initializers.hasInitializer(key))
				if (root.get(key) instanceof JSONObject) {
					try {
						Initializers.getInitializer(key).init((JSONObject) root.get(key));
					} catch (Exception e) {
						log.error("Initialization of block \"" + key + "\" failed.", e);
					}
				}
	}

	private static void unused() {
		System.out.println("This method is unused!");
	}
}
