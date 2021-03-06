package terraingen.frontend.init;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import terraingen.utils.FileHelper;

import java.io.File;
import java.io.IOException;

/**
 * A simple immutable configuration file, which accepts file of the JSON format.
 * It parses the JSON file and ignores broken ones.
 */
public class Configuration {
	private static Log log = LogFactory.getLog(Configuration.class);

	protected File file;
	protected JSONObject root;

	/**
	 * Creates an empty configuration file, just for an default initializer.
	 */
	public Configuration() {
		this.file = null;
		this.root = new JSONObject();
	}

	public Configuration(String fileName) {
		this(new File(fileName));
	}

	public Configuration(File file) {
		this.file = file;

		if (file == null) {
			log.debug("Null File object provided, defaulting to empty root.");
			this.root = new JSONObject();
		} else if (file.isDirectory()) {
			log.warn("Directory provided, should be a file, defaulting to empty root.");
			this.root = new JSONObject();
		} else {
			try {
				String content = FileHelper.readFile(file);
				this.root = new JSONObject(content);
			} catch (IOException e) {
				log.error("Error reading file content, defaulting to empty root.", e);
				this.root = new JSONObject();
			} catch (JSONException e) {
				log.error("Error parsing JSON data, defaulting to empty root", e);
				this.root = new JSONObject();
			}
		}
	}

	public File getFile() {
		return this.file;
	}

	public JSONObject getRoot() {
		return this.root;
	}
}
