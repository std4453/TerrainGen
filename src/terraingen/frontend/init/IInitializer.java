package terraingen.frontend.init;

import org.json.JSONObject;

public interface IInitializer {
	void init(JSONObject conf) throws Exception;
}
