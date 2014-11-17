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

	public NodeKey(String type) {
		put("type", type);
	}

	public String getType() {
		return get("type");
	}

	public String getId() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
