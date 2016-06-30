package terraingen.frontend.init;

import org.json.JSONObject;

public interface IInitializer {
	public void init(JSONObject conf) throws Exception;
}
