package org.test.gserver.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.test.gserver.Graph;
import org.test.gserver.GraphEdge;
import org.test.gserver.GraphException;
import org.test.gserver.GraphNode;
import org.test.gserver.GraphStorage;
import org.test.gserver.NodeKey;
import org.test.gserver.Visitor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.test.gserver.GraphException.*;

/**
 * Simple graph implantation, only supports directed graphs.
 *
 * @author serkan
 */
class GraphImpl implements Graph {

	private String id;

	private GraphStorage storage;

	private Logger logger = LoggerFactory.getLogger(GraphImpl.class);

	public GraphImpl(String id, GraphStorage storage) {
		this.id = id;
		this.storage = storage;
	}

	@Override
	public GraphNode createOrGetNode(NodeKey key) {
		return new GraphNodeProxyImpl(key, storage);
	}

	@Override
	public List<GraphEdge> getEdge(NodeKey source, NodeKey target) {
		return storage.getEdges(source, target);
	}

	@Override
	public void removeNode(NodeKey key) throws GraphException {
		beginTx();
		//		int currentVersion = storage.getCurrentVersion();

		// increase version of node
		//		storage.changeVersionOfElementByNode(key, currentVersion);

		// change state to isActive=NO
		storage.inactivateElementsByNode(key);
		commitTx();
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
	public Queue<GraphNode> detectCycle() {
		Queue<GraphNode> path = new LinkedList<>();
		// depth-first search on graph
		dfs(new Visitor() {

			@Override
			public void visit(GraphNode node) {
				List<GraphEdge> neighbors = node.getNeighbors();
				for (GraphEdge neighbor : neighbors) {
					GraphNode target = neighbor.getTarget();
					// TODO
				}
			}
		});
		return path;
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
	public void removeAll() {
		storage.removeAll();
	}

	@Override
	public void beginTx() throws GraphException {
		// increase current version
		if (storage.getTxFlag()) {
			throw new GraphException("There is an ongoing transaction", ONGOING_TX_ERR);
		} else {
			storage.increaseCurrentVersion();
			storage.changeTxFlagAs(true);
		}
		logger.debug("Graph transaction started for " + getId());
	}

	@Override
	public void commitTx() throws GraphException {
		if (storage.getTxFlag()) {
			// persist current version as last
			int currentVersion = storage.getCurrentVersion();
			storage.changeLastVersionAs(currentVersion);
			storage.changeTxFlagAs(false);
		} else {
			throw new GraphException("No Transaction to commit", NO_TX);
		}
		logger.debug("Graph transaction committed for " + getId());
	}

	@Override
	public void rollbackTx() throws GraphException {
		if (storage.getTxFlag()) {
			storage.changeTxFlagAs(false);
			// delete all current version elements
			storage.removeElementsByVersion(storage.getCurrentVersion());
			// current version becomes the same with last version
			storage.decreaseCurrentVersion();
		} else {
			throw new GraphException("There is no transaction to rollback", NO_TX);
		}
		logger.debug("Graph transaction rollback for " + getId());
	}

	@Override
	public void undo() throws GraphException {
		// throw exception if(currentVersion==0)
		int currentVersion = storage.getCurrentVersion();
		if (currentVersion == 0) {
			throw new GraphException("There is no going back, graph is already in zero state", ZERO_STATE);
		} else {
			// currentVersion -= 1;
			currentVersion = storage.decreaseCurrentVersion();
			int lastVersion = storage.getLastVersion();

			// TODO (serkan) complex trick to enable deletion undo, should be fix later
			storage.activateElementsByVersion(currentVersion);

			// inactivate if(=>currentVersion)
			while (currentVersion <= lastVersion) {
				storage.inactivateElementsByVersion(++currentVersion);
			}
		}
	}

	@Override
	public void redo() throws GraphException {
		// throw exception if(currentVersion+1==lastVersion)
		int currentVersion = storage.getCurrentVersion();
		if (currentVersion + 1 > storage.getLastVersion()) {
			throw new GraphException("The graph is already last position", LAST_POS);
		} else {
			// currentVersion += 1;
			currentVersion = storage.increaseCurrentVersion();
			// activate if(=currentVersion)
			storage.activateElementsByVersion(currentVersion);
		}
	}

	@Override
	public List<GraphNode> nodes() {
		return storage.nodes();
	}

	@Override
	public List<GraphEdge> edges() {
		return storage.edges();
	}

	@Override
	public int nodeSize() {
		return storage.nodesSize();
	}

}
