package org.test.gserver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.test.gserver.internal.GFactory;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class GraphNodeTest {


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		// TODO delete graph
	}

	@Test
	public void testGetKey() throws Exception {
		Graph g = GFactory.get(UUID.randomUUID().toString());

		String pid = "11111111111";
		NodeKey key = new NodeKey("TYPE_P");
		key.put("PID", pid);

		GraphNode node = g.createOrGetNode(key);
		NodeKey persistedKey = node.getKey();
		assertEquals(pid, persistedKey.get("PID"));

		g.removeNode(persistedKey);
	}

	@Test
	public void testAddNeighbor() throws Exception {
		Graph g = GFactory.get(UUID.randomUUID().toString());

		String pid1 = "11111111111";
		NodeKey k1 = new NodeKey("TYPE_P");
		k1.put("PID", pid1);
		String pid2 = "22222222222";
		NodeKey k2 = new NodeKey("TYPE_P");
		k1.put("PID", pid2);

		GraphNode n1 = g.createOrGetNode(k1);
		GraphNode n2 = g.createOrGetNode(k2);

		HashMap<String, String> edgeAttr = new HashMap<>();
		edgeAttr.put("RELATION", "TWISTED");

		n1.addNeighbor(n2, edgeAttr);

		assertEquals(1, n1.getNeighbors().size());

	}

	@Test
	public void testGetNeighbors() throws Exception {
		Graph g = GFactory.get(UUID.randomUUID().toString());

		String pid1 = "11111111111";
		NodeKey k1 = new NodeKey("TYPE_P");
		k1.put("PID", pid1);
		String pid2 = "22222222222";
		NodeKey k2 = new NodeKey("TYPE_P");
		k2.put("PID", pid2);

		GraphNode n1 = g.createOrGetNode(k1);
		GraphNode n2 = g.createOrGetNode(k2);

		HashMap<String, String> edgeAttr = new HashMap<>();
		edgeAttr.put("RELATION", "TWISTED");

		n1.addNeighbor(n2, edgeAttr);

		GraphEdge neighbor = n1.getNeighbors().get(0);

		GraphNode target = neighbor.getTarget();
		assertEquals(k2, target.getKey());

	}

	@Test
	public void testPutAttr() throws Exception {
		Graph g = GFactory.get(UUID.randomUUID().toString());

		String pid1 = "11111111111";
		NodeKey k1 = new NodeKey("TYPE_P");
		k1.put("PID", pid1);
		String pid2 = "22222222222";
		NodeKey k2 = new NodeKey("TYPE_P");
		k1.put("PID", pid2);

		GraphNode n1 = g.createOrGetNode(k1);
		GraphNode n2 = g.createOrGetNode(k2);

		HashMap<String, String> edgeAttr = new HashMap<>();
		edgeAttr.put("RELATION", "TWISTED");

		n1.addNeighbor(n2, edgeAttr);

		GraphEdge neighbor = n1.getNeighbors().get(0);

		assertEquals("TWISTED", neighbor.getAttr().get("RELATION"));

	}

	@Test
	public void testHashCode() throws Exception {
		// TODO
	}
}