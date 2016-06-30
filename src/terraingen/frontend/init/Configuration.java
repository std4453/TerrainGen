package terraingen.frontend.init;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import terraingen.utils.FileHelper;

/**
 * A simple immutable configuration file, which accepts file of the JSON format.
 * It parses the JSON file and ignores broken ones.
 */
public class Configuration {
	private static Log		log	= LogFactory.getLog(Configuration.class);

	protected File			file;
	protected JSONObject	root;

	/**
	 * Creates an empty configuration file, just for an default initializer.
	 */
	public Configuration() {
		file = null;
		root = new JSONObject();
	}

	public Configuration(String fileName) {
		this(new File(fileName));
	}

	public Configuration(File file) {
		this.file = file;

		if (file == null) {
			log.debug("Null File object provided, defaulting to empty root.");
			root = new JSONObject();
		} else if (file.isDirectory()) {
			log.warn("Directory provided, should be a file, defaulting to empty root.");
			root = new JSONObject();
		} else {
			try {
				String content = FileHelper.readFile(file);
				root = new JSONObject(content);
			} catch (IOException e) {
				log.error("Error reading file content, defaulting to empty root.", e);
				root = new JSONObject();
			} catch (JSONException e) {
				log.error("Error parsing JSON data, defaulting to empty root", e);
				root = new JSONObject();
			}
		}
	}

	public File getFile() {
		return file;
	}

	public JSONObject getRoot() {
		return root;
	}
}
