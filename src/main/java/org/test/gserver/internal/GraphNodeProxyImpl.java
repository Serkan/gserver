package org.test.gserver.internal;

import org.test.gserver.GraphEdge;
import org.test.gserver.GraphNode;
import org.test.gserver.GraphStorage;
import org.test.gserver.NodeKey;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * {@link org.test.gserver.GraphNode} implementation; provides lazy
 * loading of node attributes and neighbor info through storage
 * abstraction.
 *
 * @author serkan
 */
class GraphNodeProxyImpl extends GraphNode {

	private final GraphStorage storage;

	protected GraphNodeProxyImpl(NodeKey key, GraphStorage storage) {
		super(key);
		this.storage = storage;
		storage.createNodeIfNotExist(key);
	}

	@Override
	public void addNeighbor(GraphNode target, Map<String, String> attr) {
		storage.addNeighbor(getKey(), target, attr);
	}

	@Override
	public List<GraphEdge> getNeighbors() {
		List<GraphEdge> neighbors = storage.getNeighbors(getKey());
		if (neighbors != null && neighbors.size() > 0) {
			return Collections.unmodifiableList(neighbors);
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public void putAttr(Map<String, String> attr) {
		storage.putAttr(getKey(), attr);
	}

	@Override
	public Map<String, String> gettAttr() {
		return storage.getAttr(getKey());
	}

}
