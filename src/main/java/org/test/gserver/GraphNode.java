package org.test.gserver;

import java.util.List;
import java.util.Map;

/**
 * Base data structure to keep node key and type.
 *
 * @author serkan
 */
public abstract class GraphNode {

	private NodeKey key;

	protected GraphNode(NodeKey key) {
		this.key = key;
	}

	public NodeKey getKey() {
		return key;
	}

	public abstract void addNeighbor(GraphNode target, Map<String, String> attr);

	public abstract List<GraphEdge> getNeighbors();

	public abstract void putAttr(Map<String, String> attr);

	public abstract Map<String, String> gettAttr();

	@Override
	public int hashCode() {
		return key.hashCode();
	}
}
