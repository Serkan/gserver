package org.test.gserver;

import java.util.Map;

public class GraphEdge {

	private GraphNode target;

	private GraphNode source;

	private Map<String, String> attr;

	public GraphEdge(GraphNode source, GraphNode target, Map<String, String> attr) {
		this.source = source;
		this.target = target;
		this.attr = attr;
	}

	public GraphNode getTarget() {
		return target;
	}

	public GraphNode getSource() {
		return source;
	}

	public Map<String, String> getAttr() {
		return attr;
	}

}
