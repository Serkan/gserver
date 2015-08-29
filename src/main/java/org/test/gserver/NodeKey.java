package org.test.gserver;

import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

public class NodeKey extends LinkedHashMap<String, String> {

	public NodeKey(Map<String, Object> m) {
		for (Map.Entry<String, Object> entry : m.entrySet()) {
			this.put(entry.getKey(), entry.getValue().toString());
		}
	}

	@Override
	public String put(String key, String value) {
		if (value == null) {
			return null;
		}
		return super.put(key, value);
	}

	public NodeKey(String type, Pair<String, String>... attr) {
		put("type", type);
		if (attr != null && attr.length > 0) {
			for (Pair<String, String> pair : attr) {
				this.put(pair.getFirst(), pair.getSecond());
			}
		}
	}

	public String getType() {
		return get("type");
	}

	public String getId() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
