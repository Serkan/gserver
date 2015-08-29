package org.test.gserver.data;

import org.test.gserver.Graph;
import org.test.gserver.GraphEdge;
import org.test.gserver.GraphNode;
import org.test.gserver.internal.GFactory;

import java.io.IOException;
import java.util.List;

/**
 * Simple http server to response http visualization requests.
 * Its written for test purposes.
 *
 * @author serkan
 */
public class GraphServer {

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO 10 bin node 10 saniyede geliyor biras daha hizlandirabilirmiyiz bakalim
		Graph g = GFactory.get("4d91d9ae-ba10-4146-b1a6-e2b3f9825ed1");

		long nodesStart = System.currentTimeMillis();
		List<GraphNode> nodes = g.nodes();
		System.out.println("Node count : " + nodes.size());
		long nodesEnd = System.currentTimeMillis();

		System.out.println("Nodes Total : " + (nodesEnd - nodesStart));

		long edgesStart = System.currentTimeMillis();
		List<GraphEdge> edges = g.edges();
		System.out.println("Edge count : " + edges.size());
		long edgesEnd = System.currentTimeMillis();
		System.out.println("Edges Total : " + (edgesEnd - edgesStart));
	}

}
