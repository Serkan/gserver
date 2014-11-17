package org.test.gserver.internal;

import org.test.gserver.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Simple graph implantation, only supports directed graphs.
 *
 * @author serkan
 */
class GraphImpl implements Graph {

	private String id;

	private GraphStorage storage;

	public GraphImpl(String id, GraphStorage storage) {
		this.id = id;
		this.storage = storage;
	}

	@Override
	public void removeNode(NodeKey key) {
		storage.removeNode(key);
	}

	@Override
	public void traverse(Visitor visitor) {
		List<GraphNode> nodes = storage.nodes();
		for (GraphNode node : nodes) {
			visitor.visit(node);
		}
	}

	@Override
	public void bfs(Visitor visitor) {
		Queue<GraphNode> queue = new LinkedList<>();
		HashSet<GraphNode> visited = new HashSet<>();
		List<GraphNode> roots = storage.roots();
		for (GraphNode root : roots) {
			if (!visited.contains(root)) {
				visited.add(root);
				queue.add(root);
				while (!queue.isEmpty()) {
					GraphNode next = queue.poll();
					List<GraphEdge> neighbors = next.getNeighbors();
					for (GraphEdge neighbor : neighbors) {
						queue.add(neighbor.getTarget());
					}
					visitor.visit(next);
				}
			}
		}
	}

	@Override
	public void dfs(Visitor visitor) {
		// TODO depth first search
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public GraphNode createOrGetNode(NodeKey key) {
		return new GraphNodeProxyImpl(key, storage);
	}
}
