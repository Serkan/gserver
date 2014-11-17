package org.test.gserver;

import java.util.Map;

public class GraphEdge {

	private GraphNode target;

	private Map<String, String> attr;

	public GraphEdge(GraphNode target, Map<String, String> attr) {
		this.target = target;
		this.attr = attr;
	}

	public GraphNode getTarget() {
		return target;
	}

	public Map<String, String> getAttr() {
		return attr;
	}

}
