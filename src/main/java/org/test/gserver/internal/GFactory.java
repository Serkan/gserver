package org.test.gserver.internal;

import org.test.gserver.Graph;
import org.test.gserver.GraphEdge;
import org.test.gserver.GraphNode;
import org.test.gserver.NodeKey;
import org.test.gserver.Visitor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Graph factory. Abstraction between graph interface and different
 * graph implementation.
 *
 * @author serkan
 */
public final class GFactory {

	/**
	 * Hidden constructor.
	 */
	private GFactory() {
	}

	/**
	 * Create or get graph for given id.
	 *
	 * @param id id of graph
	 * @return Concrete graph impl
	 */
	public static Graph get(String id) {
		return new GraphImpl(id, new DocumentStorage(id));
	}

	public static Graph getWithLockSupport(String id) {
		return new GraphImpl(id, new DocumentStorageLockSupport(id));
	}

	/**
	 * Clones all graph with nodes and edges.
	 *
	 * @param idFrom id of graph which will be cloned
	 * @param idTo   id of newly cloned graph
	 * @return newly cloned graph reference
	 */
	public static Graph copy(String idFrom, String idTo) {
		Graph oGraph = get(idFrom);
		final Graph nGraph = get(idTo);
		final Set<GraphNode> saved = new HashSet<>();
		oGraph.traverse(new Visitor() {

			@Override
			public void visit(GraphNode node) {
				// copy node
				GraphNode nNode = cloneNode(node);
				// copy neighbors
				List<GraphEdge> oNeighbors = node.getNeighbors();
				for (GraphEdge oNeighbor : oNeighbors) {
					GraphNode oTarget = oNeighbor.getTarget();
					GraphNode nTarget;
					// if cloned before get from new graph through node key
					// else copy node and use it
					if (saved.contains(oTarget)) {
						nTarget = nGraph.createOrGetNode(oTarget.getKey());
					} else {
						nTarget = cloneNode(oTarget);
					}
					nNode.addNeighbor(nTarget, oNeighbor.getAttr());
				}
			}

			private GraphNode cloneNode(GraphNode node) {
				NodeKey oKey = node.getKey();
				GraphNode nNode = nGraph.createOrGetNode(oKey);
				nNode.putAttr(node.gettAttr());
				saved.add(nNode);
				return nNode;
			}
		});
		return nGraph;
	}

}
