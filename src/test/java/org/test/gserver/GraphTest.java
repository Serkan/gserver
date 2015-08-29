package org.test.gserver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.test.gserver.internal.GFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

public class GraphTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		// TODO delete graph
	}

	@Test
	public void testCreateOrGetNode() throws Exception {
		Graph g = GFactory.get(UUID.randomUUID().toString());

		String pid = "11111111111";
		NodeKey key = new NodeKey("TYPE_P");
		key.put("PID", pid);

		GraphNode node = g.createOrGetNode(key);
		NodeKey persistedKey = node.getKey();
		assertEquals(pid, persistedKey.get("PID"));
		assertEquals(1, g.nodeSize());

		g.removeNode(persistedKey);
	}

	@Test
	public void testGetEdge() {
		String graphId = UUID.randomUUID().toString();
		{
			Graph g = GFactory.get(graphId);
			NodeKey k1 = new NodeKey("TYPE_P", new Pair<>("PID", "1"));
			NodeKey k2 = new NodeKey("TYPE_P", new Pair<>("PID", "2"));
			GraphNode n1 = g.createOrGetNode(k1);
			GraphNode n2 = g.createOrGetNode(k2);
			n1.addNeighbor(n2, new HashMap<String, String>());
		}
		{
			NodeKey k1 = new NodeKey("TYPE_P", new Pair<>("PID", "1"));
			NodeKey k2 = new NodeKey("TYPE_P", new Pair<>("PID", "2"));
			Graph g = GFactory.get(graphId);
			List<GraphEdge> edges = g.getEdge(k1, k2);
			for (GraphEdge graphEdge : edges) {
				assertTrue(graphEdge.getSource().getKey().equals(k1));
				assertTrue(graphEdge.getTarget().getKey().equals(k2));
			}
		}
	}

	@Test
	public void testGetEdgeWithAttributes() {
		String graphId = UUID.randomUUID().toString();
		{
			Graph g = GFactory.get(graphId);
			NodeKey k1 = new NodeKey("TYPE_P", new Pair<>("PID", "1"));
			NodeKey k2 = new NodeKey("TYPE_P", new Pair<>("PID", "2"));
			GraphNode n1 = g.createOrGetNode(k1);
			GraphNode n2 = g.createOrGetNode(k2);
			HashMap<String, String> attr1 = new HashMap<String, String>();
			attr1.put("edgeId", "edge1");
			attr1.put("edgeAttr", "something");
			n1.addNeighbor(n2, attr1);
			HashMap<String, String> attr2 = new HashMap<String, String>();
			attr2.put("edgeId", "edge2");
			n1.addNeighbor(n2, attr2);
			n1.addNeighbor(n2, new HashMap<String, String>());
		}
		{
			NodeKey k1 = new NodeKey("TYPE_P", new Pair<>("PID", "1"));
			NodeKey k2 = new NodeKey("TYPE_P", new Pair<>("PID", "2"));
			Graph g = GFactory.get(graphId);
			List<GraphEdge> edges = g.getEdge(k1, k2);
			assertEquals(edges.size(), 3);
		}
	}

	@Test
	public void testRemoveNode() throws Exception {
		Graph g = GFactory.get(UUID.randomUUID().toString());

		String pid = "11111111111";
		NodeKey key = new NodeKey("TYPE_P");
		key.put("PID", pid);

		// create
		g.beginTx();
		g.createOrGetNode(key);
		g.commitTx();

		assertEquals(1, g.nodeSize());

		// remove
		g.removeNode(key);

		assertEquals(0, g.nodeSize());
	}

	@Test
	public void testTraverse() throws Exception {
		Graph g = GFactory.get(UUID.randomUUID().toString());

		NodeKey k1 = new NodeKey("TYPE_P");
		k1.put("PID", "1111111111");
		NodeKey k2 = new NodeKey("TYPE_P");
		k2.put("PID", "2222222222");

		g.createOrGetNode(k1);
		g.createOrGetNode(k2);

		final int c[] = { 0 };
		// graph must contains exactly two node after above insertions
		g.traverse(new Visitor() {

			@Override
			public void visit(GraphNode node) {
				c[0] = c[0] + 1;
			}
		});

		assertEquals(2, c[0]);
	}

	@Test
	public void testNormalTx() throws GraphException {
		Graph g = GFactory.get(UUID.randomUUID().toString());

		g.beginTx();
		NodeKey k1 = new NodeKey("TYPE_P");
		k1.put("PID", "1111111111");

		g.createOrGetNode(k1);

		g.commitTx();
	}

	@Test(expected = GraphException.class)
	public void testTxWithoutCtx() throws GraphException {
		Graph g = GFactory.get(UUID.randomUUID().toString());

		NodeKey k1 = new NodeKey("TYPE_P");
		k1.put("PID", "1111111111");

		g.createOrGetNode(k1);

		g.commitTx();
	}

	@Test
	public void testUndoWithOneNode() throws GraphException {
		Graph g = GFactory.get(UUID.randomUUID().toString());

		g.beginTx();
		String pid = "11111111111";
		NodeKey key = new NodeKey("TYPE_P");
		key.put("PID", pid);
		g.createOrGetNode(key);
		g.commitTx();

		g.undo();

		assertEquals(0, g.nodeSize());
		g.removeAll();
	}

	@Test
	public void testUndoAddForSameNode() throws GraphException {
		Graph g = GFactory.get(UUID.randomUUID().toString());

		//prepare node key
		String pid = "11111111111";
		NodeKey key = new NodeKey("TYPE_P");
		key.put("PID", pid);

		g.beginTx();
		g.createOrGetNode(key);
		g.commitTx();

		g.undo();

		g.beginTx();
		g.createOrGetNode(key);
		g.commitTx();

		assertEquals(1, g.nodeSize());
		g.removeAll();
	}

	@Test
	public void testUndoWithNeighbor() throws GraphException {
		Graph g = GFactory.get(UUID.randomUUID().toString());

		g.beginTx();
		String pid = "11111111111";
		NodeKey k1 = new NodeKey("TYPE_P");
		k1.put("PID", pid);
		GraphNode n1 = g.createOrGetNode(k1);
		g.commitTx();

		// create and add a neighbor
		g.beginTx();
		String pid2 = "2222222222";
		NodeKey k2 = new NodeKey("TYPE_P");
		k2.put("PID", pid2);
		GraphNode n2 = g.createOrGetNode(k2);
		n1.addNeighbor(n2, new HashMap<String, String>(0));
		g.commitTx();

		g.undo();

		assertEquals(1, g.nodeSize());
		assertEquals(0, n1.getNeighbors().size());
	}

	@Test
	public void testUndoWithExpandThreeSteps() throws GraphException {
		// step 1 add one node
		String graphId = UUID.randomUUID().toString();
		{
			Graph g = GFactory.get(graphId);
			g.beginTx();
			String pid = "1";
			NodeKey k1 = new NodeKey("TYPE_P");
			k1.put("PID", pid);
			g.createOrGetNode(k1);
			g.commitTx();
		}
		// step 2 add neighbors
		{
			Graph g = GFactory.get(graphId);
			g.beginTx();
			String pid = "1";
			NodeKey k1 = new NodeKey("TYPE_P");
			k1.put("PID", pid);
			GraphNode n1 = g.createOrGetNode(k1);

			String pid2 = "2";
			NodeKey k2 = new NodeKey("TYPE_P");
			k2.put("PID", pid2);
			GraphNode n2 = g.createOrGetNode(k2);
			String pid3 = "3";
			NodeKey k3 = new NodeKey("TYPE_P");
			k3.put("PID", pid3);
			GraphNode n3 = g.createOrGetNode(k3);

			HashMap<String, String> attr = new HashMap<String, String>(0);
			n1.addNeighbor(n2, attr);
			n1.addNeighbor(n3, attr);

			g.commitTx();
		}
		// step 3 add more neighbors
		{
			Graph g = GFactory.get(graphId);
			g.beginTx();
			String pid = "1";
			NodeKey k1 = new NodeKey("TYPE_P");
			k1.put("PID", pid);
			GraphNode n1 = g.createOrGetNode(k1);

			String pid2 = "2";
			NodeKey k2 = new NodeKey("TYPE_P");
			k2.put("PID", pid2);
			GraphNode n2 = g.createOrGetNode(k2);

			String pid4 = "4";
			NodeKey k4 = new NodeKey("TYPE_P");
			k4.put("PID", pid4);
			GraphNode n4 = g.createOrGetNode(k4);

			HashMap<String, String> attr = new HashMap<String, String>(0);
			n2.addNeighbor(n4, attr);
			n2.addNeighbor(n1, attr);

			g.commitTx();
		}

		// assertions
		Graph g = GFactory.get(graphId);
		assertEquals(4, g.nodeSize());

		g.undo();
		assertEquals(3, g.nodeSize());

		g.traverse(new Visitor() {

			@Override
			public void visit(GraphNode node) {
				assertNotNull(node.getKey());
			}
		});

		NodeKey k1 = new NodeKey("TYPE_P", new Pair<>("PID", "1"));
		GraphNode n1 = g.createOrGetNode(k1);
		assertEquals(2, n1.getNeighbors().size());

		NodeKey k2 = new NodeKey("TYPE_P", new Pair<>("PID", "2"));
		GraphNode n2 = g.createOrGetNode(k2);
		assertEquals(0, n2.getNeighbors().size());
	}

	//	@Test
	//	public void testUndoForOnlyEdge() throws GraphException {
	//		String graphId = UUID.randomUUID().toString();
	//
	//		{
	//			Graph g = GFactory.get(graphId);
	//			g.beginTx();
	//			String pid = "1";
	//			NodeKey k1 = new NodeKey("TYPE_P");
	//			k1.put("PID", pid);
	//			g.createOrGetNode(k1);
	//			g.commitTx();
	//		}
	//
	//		{
	//			Graph g = GFactory.get(graphId);
	//			// create and add a neighbor
	//			g.beginTx();
	//			NodeKey k1 = new NodeKey("TYPE_P", new Pair<>("PID", "1"));
	//			GraphNode n1 = g.createOrGetNode(k1);
	//
	//			String pid2 = "2";
	//			NodeKey k2 = new NodeKey("TYPE_P");
	//			k2.put("PID", pid2);
	//			GraphNode n2 = g.createOrGetNode(k2);
	//			n1.addNeighbor(n2, new HashMap<String, String>(0));
	//			g.commitTx();
	//		}
	//
	//		{
	//			Graph g = GFactory.get(graphId);
	//			// create and add a neighbor
	//			g.beginTx();
	//			NodeKey k1 = new NodeKey("TYPE_P", new Pair<>("PID", "1"));
	//			GraphNode n1 = g.createOrGetNode(k1);
	//
	//			NodeKey k2 = new NodeKey("TYPE_P", new Pair<>("PID", "2"));
	//			GraphNode n2 = g.createOrGetNode(k2);
	//			n2.addNeighbor(n1, new HashMap<String, String>(0));
	//			g.commitTx();
	//		}
	//
	//		Graph g = GFactory.get(graphId);
	//
	//		g.undo();
	//
	//		NodeKey k2 = new NodeKey("TYPE_P", new Pair<>("PID", "2"));
	//		GraphNode n2 = g.createOrGetNode(k2);
	//		assertEquals(0, n2.getNeighbors().size());
	//
	//	}

	@Test
	public void testUndoWithNeighborAddSame() throws GraphException {
		Graph g = GFactory.get(UUID.randomUUID().toString());

		// prepare source key
		String pid = "11111111111";
		NodeKey k1 = new NodeKey("TYPE_P");
		k1.put("PID", pid);
		// prepare target key
		String pid2 = "2222222222";
		NodeKey k2 = new NodeKey("TYPE_P");
		k2.put("PID", pid2);

		g.beginTx();
		GraphNode n1 = g.createOrGetNode(k1);
		g.commitTx();

		// create and add a neighbor
		g.beginTx();
		GraphNode n2 = g.createOrGetNode(k2);
		n1.addNeighbor(n2, new HashMap<String, String>(0));
		g.commitTx();

		g.undo(); // revert edge relation

		g.beginTx();
		n2 = g.createOrGetNode(k2);
		n1.addNeighbor(n2, new HashMap<String, String>(0));
		g.commitTx();

		assertEquals(1, n1.getNeighbors().size());
		assertEquals(2, g.nodeSize());
	}

	@Test
	public void testRedoWithOneNode() throws GraphException {
		Graph g = GFactory.get(UUID.randomUUID().toString());

		g.beginTx();
		String pid = "11111111111";
		NodeKey key = new NodeKey("TYPE_P");
		key.put("PID", pid);
		g.createOrGetNode(key);
		g.commitTx();

		g.undo();
		g.redo();

		assertEquals(1, g.nodeSize());
	}

	@Test
	public void testRedoWithOneRelation() throws GraphException {
		String graphId = UUID.randomUUID().toString();

		Graph g = GFactory.get(graphId);

		// prepare source key
		String pid1 = "11111111111";
		NodeKey k1 = new NodeKey("TYPE_P");
		k1.put("PID", pid1);
		// prepare target key
		String pid2 = "2222222222";
		NodeKey k2 = new NodeKey("TYPE_P");
		k2.put("PID", pid2);

		g.beginTx();
		GraphNode n1 = g.createOrGetNode(k1);
		g.commitTx();

		g = GFactory.get(graphId);
		n1 = g.createOrGetNode(k1);

		g.beginTx();
		GraphNode n2 = g.createOrGetNode(k2);
		n1.addNeighbor(n2, new HashMap<String, String>(0));
		g.commitTx();

		g.undo();
		g.redo();

		assertEquals(1, n1.getNeighbors().size());
	}

	@Test
	public void testRedoWithTwoRelation() throws GraphException {
		String graphId = UUID.randomUUID().toString();

		Graph g = GFactory.get(graphId);

		// prepare source key
		String pid1 = "11111111111";
		NodeKey k1 = new NodeKey("TYPE_P");
		k1.put("PID", pid1);
		// prepare target key
		String pid2 = "2222222222";
		NodeKey k2 = new NodeKey("TYPE_P");
		k2.put("PID", pid2);
		String pid3 = "3333333333";
		NodeKey k3 = new NodeKey("TYPE_P");
		k3.put("PID", pid3);

		g.beginTx();
		GraphNode n1 = g.createOrGetNode(k1);
		g.commitTx();

		g = GFactory.get(graphId);

		g.beginTx();
		n1 = g.createOrGetNode(k1);

		GraphNode n2 = g.createOrGetNode(k2);
		GraphNode n3 = g.createOrGetNode(k3);

		n1.addNeighbor(n2, new HashMap<String, String>(0));
		n1.addNeighbor(n3, new HashMap<String, String>(0));
		g.commitTx();

		g.undo();
		g.redo();

		assertEquals(2, n1.getNeighbors().size());
	}

	@Test
	public void testUndoDeletion() throws GraphException {
		String graphId = UUID.randomUUID().toString();
		{
			Graph g = GFactory.get(graphId);
			g.beginTx();
			NodeKey k1 = new NodeKey("TYPE_P", new Pair<>("PID", "1"));
			g.createOrGetNode(k1);
			g.commitTx();
		}
		{
			Graph g = GFactory.get(graphId);
			NodeKey k1 = new NodeKey("TYPE_P", new Pair<>("PID", "1"));
			g.removeNode(k1);
		}
		// assertions
		Graph g = GFactory.get(graphId);
		g.undo();
		assertEquals(1, g.nodeSize());
	}

	@Test
	public void addSameNeighbor() throws GraphException {
		String graphId = UUID.randomUUID().toString();
		Map<String, String> edge = new HashMap<>();
		edge.put("RELATION", "COMPLICATED");
		{
			Graph g = GFactory.get(graphId);
			g.beginTx();
			NodeKey k1 = new NodeKey("TYPE_P", new Pair<>("PID", "1"));
			NodeKey k2 = new NodeKey("TYPE_P", new Pair<>("PID", "2"));
			GraphNode n1 = g.createOrGetNode(k1);
			GraphNode n2 = g.createOrGetNode(k2);
			n1.addNeighbor(n2, edge);
			g.commitTx();
		}
		{
			Graph g = GFactory.get(graphId);
			g.beginTx();
			NodeKey k1 = new NodeKey("TYPE_P", new Pair<>("PID", "1"));
			NodeKey k2 = new NodeKey("TYPE_P", new Pair<>("PID", "2"));
			GraphNode n1 = g.createOrGetNode(k1);
			GraphNode n2 = g.createOrGetNode(k2);
			n1.addNeighbor(n2, edge);
			g.commitTx();
		}
		Graph g = GFactory.get(graphId);
		NodeKey k1 = new NodeKey("TYPE_P", new Pair<>("PID", "1"));
		GraphNode n1 = g.createOrGetNode(k1);
		assertEquals(1, n1.getNeighbors().size());
	}

	@Test
	public void testAddTwoNeighborWithSameAttributes() throws GraphException {
		String graphId = UUID.randomUUID().toString();
		Map<String, String> edge = new HashMap<>();
		edge.put("RELATION", "NOT_IMPLEMENTED");
		{
			Graph g = GFactory.get(graphId);
			g.beginTx();
			NodeKey k1 = new NodeKey("TYPE_P", new Pair<>("PID", "1"));
			NodeKey k2 = new NodeKey("TYPE_P", new Pair<>("PID", "2"));
			GraphNode n1 = g.createOrGetNode(k1);
			GraphNode n2 = g.createOrGetNode(k2);
			n1.addNeighbor(n2, edge);
			g.commitTx();
		}
		{
			Graph g = GFactory.get(graphId);
			g.beginTx();
			NodeKey k1 = new NodeKey("TYPE_P", new Pair<>("PID", "1"));
			NodeKey k3 = new NodeKey("TYPE_P", new Pair<>("PID", "3"));
			GraphNode n1 = g.createOrGetNode(k1);
			GraphNode n3 = g.createOrGetNode(k3);
			n1.addNeighbor(n3, edge);
			g.commitTx();
		}
		Graph g = GFactory.get(graphId);
		NodeKey k1 = new NodeKey("TYPE_P", new Pair<>("PID", "1"));
		GraphNode n1 = g.createOrGetNode(k1);
		assertEquals(2, n1.getNeighbors().size());
	}

	@Test
	public void testNodes() {
		String graphId = UUID.randomUUID().toString();
		{
			Graph g = GFactory.get(graphId);
			NodeKey k1 = new NodeKey("TYPE_P", new Pair<>("PID", "1"));
			NodeKey k2 = new NodeKey("TYPE_P", new Pair<>("PID", "2"));
			g.createOrGetNode(k1);
			g.createOrGetNode(k2);
		}
		{
			Graph g = GFactory.get(graphId);
			assertEquals(2, g.nodes().size());
		}
	}

	@Test
	public void testEdges() {
		String graphId = UUID.randomUUID().toString();
		{
			Graph g = GFactory.get(graphId);
			NodeKey k1 = new NodeKey("TYPE_P", new Pair<>("PID", "1"));
			NodeKey k2 = new NodeKey("TYPE_P", new Pair<>("PID", "2"));
			GraphNode n1 = g.createOrGetNode(k1);
			GraphNode n2 = g.createOrGetNode(k2);
			n1.addNeighbor(n2, new HashMap<String, String>());

		}
		{
			Graph g = GFactory.get(graphId);
			assertEquals(1, g.edges().size());
		}
	}

	@Test
	public void testBfs() throws Exception {
		// TODO
	}

	@Test
	public void testDetectCycle() throws Exception {
		// TODO
	}

	@Test
	public void testDfs() throws Exception {
		// TODO
	}

	@Test
	public void testGetId() throws Exception {
		// TODO
	}

	@Test
	public void testRemoveAll() throws Exception {
		// TODO
	}
}