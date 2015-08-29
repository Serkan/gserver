package org.test.gserver.internal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.test.gserver.Graph;
import org.test.gserver.GraphNode;
import org.test.gserver.NodeKey;
import org.test.gserver.Pair;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class GFactoryTest {

	private String id;

	@Before
	public void setUp() throws Exception {
		id = UUID.randomUUID().toString();
	}

	@After
	public void tearDown() throws Exception {
		// TODO delete graph
	}

	@Test
	public void testGet() throws Exception {
		Graph graph = GFactory.get(id);

		assertEquals(id, graph.getId());
		assertEquals(0, graph.nodeSize());
	}

	@Test
	public void testCopyOneNodeGraph() throws Exception {
		{
			Graph g = GFactory.get(id);
			g.beginTx();
			String pid = "1";
			NodeKey k1 = new NodeKey("TYPE_P");
			k1.put("PID", pid);
			g.createOrGetNode(k1);
			g.commitTx();
		}

		String nId = UUID.randomUUID().toString();

		GFactory.copy(id, nId);

		Graph oGraph = GFactory.get(id);
		Graph nGraph = GFactory.get(nId);

		assertEquals(oGraph.nodeSize(), nGraph.nodeSize());
	}

	@Test
	public void testCopyTwoNodeGraphWithEdges() throws Exception {
		{
			Graph g = GFactory.get(id);
			g.beginTx();
			String pid = "1";
			NodeKey k1 = new NodeKey("TYPE_P");
			k1.put("PID", pid);
			GraphNode n1 = g.createOrGetNode(k1);

			String pid2 = "2";
			NodeKey k2 = new NodeKey("TYPE_P");
			k2.put("PID", pid2);
			GraphNode n2 = g.createOrGetNode(k2);

			n1.addNeighbor(n2, new HashMap<String, String>(0));
			n2.addNeighbor(n1, new HashMap<String, String>(0));

			g.commitTx();
		}

		String nId = UUID.randomUUID().toString();

		GFactory.copy(id, nId);

		Graph oGraph = GFactory.get(id);
		Graph nGraph = GFactory.get(nId);

		// assert graph sizes
		assertEquals(oGraph.nodeSize(), nGraph.nodeSize());
		// assert neighbor sizes

		NodeKey k1 = new NodeKey("TYPE_P", new Pair<>("PID", "1"));
		NodeKey k2 = new NodeKey("TYPE_P", new Pair<>("PID", "2"));

		GraphNode oN1 = oGraph.createOrGetNode(k1);
		GraphNode oN2 = oGraph.createOrGetNode(k2);
		GraphNode nN1 = nGraph.createOrGetNode(k1);
		GraphNode nN2 = nGraph.createOrGetNode(k2);

		assertEquals(oN1.getNeighbors().size(), nN1.getNeighbors().size());
		assertEquals(oN2.getNeighbors().size(), nN2.getNeighbors().size());
	}

}