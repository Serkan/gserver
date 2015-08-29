package org.test.gserver.internal;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.test.gserver.GraphEdge;
import org.test.gserver.GraphNode;
import org.test.gserver.GraphStorage;
import org.test.gserver.NodeKey;
import org.test.gserver.Pair;
import org.test.gserver.internal.nosql.DocumentDAO;
import org.test.gserver.internal.nosql.MongoDAO;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class DocumentStorage implements GraphStorage {

	private final String graphId;

	private Logger logger = LoggerFactory.getLogger(DocumentStorage.class);

	private DocumentDAO documentDAO = new MongoDAO();

	public DocumentStorage(String graphId) {
		// you must set graph id before everything because storage highly dependent on graphId
		this.graphId = graphId;
		// ensure graph is exist in storage
		if (!graphExists()) {
			createGraph();
		}
		// ensure indexes
		Map<String, Object> document = new HashMap<>();
		document.put("graphId", 1);
		document.put("key", 1);
		documentDAO.createIndex(document);
	}

	@Override
	public List<GraphNode> roots() {
		// TODO (serkan) add isRoot flag while creating nodes and when neighbors added set the flag false
		// TODO (serkan) retrieve only rootFlag=true nodes
		return null;
	}

	@Override
	public void removeNode(NodeKey key) {
		Map<String, Object> document = new HashMap<>();
		document.put("graphId", graphId);
		document.put("documentType", "node");
		Object oID = createOrGetKeyDocumentAndGetId(key);
		document.put("key", oID);
		documentDAO.delete(document);

		removeOutgoingEdges(key);
		removeIncomingEdges(key);

		// remove key container document
		deleteNodeKey(oID);
	}

	@Override
	public void createNodeIfNotExist(NodeKey key) {
		Map<String, Object> document = new HashMap<>();
		document.put("graphId", graphId);
		document.put("documentType", "node");
		document.put("isRoot", true);
		Object oID = createOrGetKeyDocumentAndGetId(key);
		document.put("isActive", true);
		document.put("key", oID);
		if (documentDAO.count(document) < 1) {
			document.put("version", currentVersion());
			documentDAO.save(document);
		}
	}

	@Override
	public List<GraphEdge> getEdges(NodeKey source, NodeKey target) {
		Object sourceObjId = createOrGetKeyDocumentAndGetId(source);
		Object targetObjId = createOrGetKeyDocumentAndGetId(target);

		Map<String, Object> edgeExample = new HashMap<>();
		edgeExample.put("documentType", "edge");
		edgeExample.put("graphId", graphId);
		edgeExample.put("isActive", true);
		edgeExample.put("source", sourceObjId);
		edgeExample.put("target", targetObjId);

		List<Map<String, Object>> edges = documentDAO.find(edgeExample);

		if (edges.size() == 0) {
			return null;
		}
		List<GraphEdge> result = new LinkedList<>();
		for (Map<String, Object> e : edges) {
			NodeKey sourceKey = getNodeKey(e.get("source"));
			NodeKey targetKey = getNodeKey(e.get("target"));
			Object attr = e.get("attr");

			GraphNode sourceNode = new GraphNodeProxyImpl(sourceKey, this, false);
			GraphNode targetNode = new GraphNodeProxyImpl(targetKey, this, false);

			GraphEdge edge = new GraphEdge(sourceNode, targetNode, (Map<String, String>) attr);
			result.add(edge);
		}
		return result;
	}

	@Override
	public void addNeighbor(NodeKey sourceKey, GraphNode target, Map<String, String> attr) {
		// check if exist
		if (!existEdge(sourceKey, target.getKey(), attr)) {
			// create edge
			createEdge(sourceKey, target.getKey(), attr);
		}
	}

	@Override
	public List<GraphEdge> getNeighbors(NodeKey key) {
		List<Pair<NodeKey, Map<String, String>>> outgoingList = getOutgoingList(key);
		if (outgoingList.size() != 0) {
			List<GraphEdge> result = new LinkedList<>();
			for (Pair<NodeKey, Map<String, String>> nodeKeyMapPair : outgoingList) {
				GraphNode targetNode = new GraphNodeProxyImpl(nodeKeyMapPair.getFirst(), this, false);
				GraphNode sourceNode = new GraphNodeProxyImpl(key, this, false);
				GraphEdge edge = new GraphEdge(sourceNode, targetNode, nodeKeyMapPair.getSecond());
				result.add(edge);
			}
			return result;
		} else {
			return null;
		}
	}

	@Override
	public void putAttr(NodeKey key, Map<String, String> attr) {
		Map<String, Object> document = new HashMap<>();
		document.put("graphId", graphId);
		document.put("documentType", "node");
		document.put("isActive", true);
		Object oID = createOrGetKeyDocumentAndGetId(key);
		document.put("key", oID);
		Map<String, Object> old = documentDAO.findOne(document);
		Map<String, Object> next = copyDocument(old);
		// TODO (serkan) attr must be append not overwrite
		next.put("attr", attr);
		documentDAO.update(old, next);
	}

	@Override
	public Map<String, String> getAttr(NodeKey key) {
		Map<String, Object> document = new HashMap<>();
		document.put("graphId", graphId);
		document.put("documentType", "node");
		document.put("isActive", true);
		Object oID = createOrGetKeyDocumentAndGetId(key);

		document.put("key", oID);
		Map<String, Object> node = documentDAO.findOne(document);

		// TODO mongo bagimliligi olustu DAO.getAttr(Obj) gibi bit metod ile cozulebilir
		BasicDBObject attr = (BasicDBObject) node.get("attr");
		Map result;
		if (attr != null) {
			result = attr.toMap();
		} else {
			result = new HashMap<>(1);
		}
		return result;
	}

	@Override
	public void removeAll() {
		Map<String, Object> document = new HashMap<>();
		document.put("graphId", graphId);
		documentDAO.delete(document);
	}

	@Override
	public void changeTxFlagAs(boolean flag) {
		changeGraphDocumentState("txFlag", flag);
	}

	@Override
	public boolean getTxFlag() {
		return Boolean.parseBoolean(getGraphDocument().get("txFlag").toString());
	}

	@Override
	public int increaseCurrentVersion() {
		int currentVersion = currentVersion();
		changeGraphDocumentState("currentVersion", ++currentVersion);
		return currentVersion();
	}

	@Override
	public int decreaseCurrentVersion() {
		int currentVersion = currentVersion();
		changeGraphDocumentState("currentVersion", --currentVersion);
		return currentVersion();
	}

	@Override
	public int getCurrentVersion() {
		return currentVersion();
	}

	@Override
	public int getLastVersion() {
		return Integer.parseInt(getGraphDocument().get("lastVersion").toString());
	}

	@Override
	public void changeLastVersionAs(int version) {
		changeGraphDocumentState("lastVersion", version);
	}

	@Override
	public void removeElementsByVersion(int version) {
		// find nodes for this version
		mapNodesByVersion(version, new DocumentVisitor() {

			@Override
			public void visit(Map<String, Object> obj) {
				documentDAO.delete(obj);
				// find back edges
				// TODO find back edges and remove if(EdgeVersion==version))
			}
		});
	}

	@Override
	public void inactivateElementsByVersion(int version) {
		changeElementsStateByVersion(version, false);
	}

	@Override
	public void activateElementsByVersion(int version) {
		changeElementsStateByVersion(version, true);
	}

	@Override
	public int nodesSize() {
		Map<String, Object> document = new HashMap<>();
		document.put("graphId", graphId);
		document.put("isActive", true);
		return (int) documentDAO.countKeys(document);
	}

	@Override
	public void inactivateElementsByNode(final NodeKey key) {
		final boolean state = false;
		mapNodesByKey(key, new DocumentVisitor() {

			@Override
			public void visit(Map<String, Object> dbObj) {
				Map<String, Object> next = copyDocument(dbObj);
				next.put("isActive", state);
				documentDAO.update(dbObj, next);

				// also you have to change state of idContainer of the node
				Map<String, Object> example = new HashMap<>();
				Object currentNodeKeyRaw = next.get("key");
				example.put("_id", currentNodeKeyRaw);
				example.put("graphId", graphId);

				Map<String, Object> foundKey = documentDAO.findKey(example);
				Map<String, Object> keyCopy = copyDocument(foundKey);

				// inactivate outgoing edges
				//				NodeKey nodeKey = getNodeKey(key);
				changeOutgoingEdgesStateByVersion(key, -1, false);
				// inactivate incoming edges
				changeIncomingEdgesStateByVersion(key, -1, false);

				keyCopy.put("isActive", state);
				documentDAO.updateKey(foundKey, keyCopy);

			}
		});
	}

	@Override
	public boolean atomicLock(String owner, NodeKey key, long maxLongTimeInNano) {
		Map<String, Object> lockDoc = new HashMap<>();
		lockDoc.put("graphId", graphId);
		lockDoc.put("key", key);
		lockDoc.put("owner", owner);
		// set expire time 'maxLockTime' milliseconds after its created
		lockDoc.put("expires", System.nanoTime() + maxLongTimeInNano);

		Map<String, Object> queryDoc = new HashMap<>();
		queryDoc.put("graphId", graphId);
		queryDoc.put("key", key);
		// set expire time 'maxLockTime' milliseconds after its created
		queryDoc.put("expires", System.nanoTime());

		return documentDAO.upsertLock(lockDoc, queryDoc);
	}

	@Override
	public void releaseLock(String owner, NodeKey key) {
		Map<String, Object> lockDoc = new HashMap<>();
		lockDoc.put("key", key);
		lockDoc.put("graphId", graphId);
		documentDAO.deleteLock(lockDoc);
	}

	private void createGraph() {
		Map<String, Object> document = new HashMap<>();
		document.put("graphId", graphId);
		document.put("documentType", "header");
		document.put("currentVersion", 0);
		document.put("lastVersion", 0);
		document.put("txFlag", false);
		documentDAO.save(document);
	}

	private Map<String, Object> getGraphDocument() {
		Map<String, Object> document = new HashMap<>();
		document.put("graphId", graphId);
		document.put("documentType", "header");
		return documentDAO.findOne(document);
	}

	private boolean graphExists() {
		Map<String, Object> document = new HashMap<>();
		document.put("graphId", graphId);
		document.put("documentType", "header");
		return documentDAO.count(document) != 0;
	}

	@Override
	public List<GraphEdge> edges() {
		Map<String, Object> edgeExample = new HashMap<>();
		edgeExample.put("documentType", "edge");
		edgeExample.put("graphId", graphId);
		edgeExample.put("isActive", true);
		List<Map<String, Object>> edges = documentDAO.find(edgeExample);

		List<GraphEdge> result = new LinkedList<>();
		for (Map<String, Object> edgeMap : edges) {
			NodeKey sourceKey = getNodeKey(edgeMap.get("source"));
			NodeKey targetKey = getNodeKey(edgeMap.get("target"));
			Object attr = edgeMap.get("attr");

			GraphNode sourceNode = new GraphNodeProxyImpl(sourceKey, this, false);
			GraphNode targetNode = new GraphNodeProxyImpl(targetKey, this, false);

			GraphEdge edge = new GraphEdge(sourceNode, targetNode, (Map<String, String>) attr);
			result.add(edge);
		}

		return result;
	}

	private void createEdge(NodeKey source, NodeKey target, Map<String, String> attr) {
		Map<String, Object> edge = new HashMap<>();
		edge.put("documentType", "edge");
		edge.put("graphId", graphId);
		edge.put("source", createOrGetKeyDocumentAndGetId(source));
		edge.put("target", createOrGetKeyDocumentAndGetId(target));
		edge.put("attr", attr);
		edge.put("version", currentVersion());
		edge.put("isActive", true);
		documentDAO.save(edge);
	}

	private void removeEdge(NodeKey source, NodeKey target) {
		Map<String, Object> edge = new HashMap<>();
		edge.put("documentType", "edge");
		edge.put("graphId", graphId);
		edge.put("source", createOrGetKeyDocumentAndGetId(source));
		edge.put("target", createOrGetKeyDocumentAndGetId(target));
		edge.put("isActive", true);
		documentDAO.delete(edge);
	}

	private void removeOutgoingEdges(NodeKey source) {
		Map<String, Object> edge = new HashMap<>();
		edge.put("documentType", "edge");
		edge.put("graphId", graphId);
		edge.put("source", createOrGetKeyDocumentAndGetId(source));
		edge.put("isActive", true);
		documentDAO.delete(edge);
	}

	private void removeIncomingEdges(NodeKey target) {
		Map<String, Object> edge = new HashMap<>();
		edge.put("documentType", "edge");
		edge.put("graphId", graphId);
		edge.put("target", createOrGetKeyDocumentAndGetId(target));
		edge.put("isActive", true);
		documentDAO.delete(edge);
	}

	private boolean existEdge(NodeKey source, NodeKey target, Map<String, String> attr) {
		Map<String, Object> edge = new HashMap<>();
		edge.put("documentType", "edge");
		edge.put("graphId", graphId);
		edge.put("source", createOrGetKeyDocumentAndGetId(source));
		edge.put("target", createOrGetKeyDocumentAndGetId(target));
		edge.put("attr", attr);
		edge.put("isActive", true);
		return documentDAO.count(edge) > 0;
	}

	private void changeEdgeStateByVersion(int version, boolean state) {
		Map<String, Object> edgeExample = new HashMap<>();
		edgeExample.put("documentType", "edge");
		edgeExample.put("graphId", graphId);
		if (version > 0) {
			edgeExample.put("version", version);
		}

		List<Map<String, Object>> edges = documentDAO.find(edgeExample);
		for (Map<String, Object> edge : edges) {
			Map<String, Object> newEdge = copyDocument(edge);
			newEdge.put("isActive", state);
			documentDAO.update(edge, newEdge);
		}
	}

	private void changeOutgoingEdgesStateByVersion(NodeKey source, int version, boolean state) {
		Map<String, Object> edgeExample = new HashMap<>();
		edgeExample.put("documentType", "edge");
		edgeExample.put("graphId", graphId);
		edgeExample.put("source", createOrGetKeyDocumentAndGetId(source, false));
		if (version > 0) {
			edgeExample.put("version", version);
		}

		List<Map<String, Object>> edges = documentDAO.find(edgeExample);
		for (Map<String, Object> edge : edges) {
			Map<String, Object> newEdge = copyDocument(edge);
			newEdge.put("isActive", state);
			documentDAO.update(edge, newEdge);
		}
	}

	private void changeIncomingEdgesStateByVersion(NodeKey target, int version, boolean state) {
		Map<String, Object> edgeExample = new HashMap<>();
		edgeExample.put("documentType", "edge");
		edgeExample.put("graphId", graphId);
		edgeExample.put("target", createOrGetKeyDocumentAndGetId(target, false));
		if (version > 0) {
			edgeExample.put("version", version);
		}

		List<Map<String, Object>> edges = documentDAO.find(edgeExample);
		for (Map<String, Object> edge : edges) {
			Map<String, Object> newEdge = copyDocument(edge);
			newEdge.put("isActive", state);
			documentDAO.update(edge, newEdge);
		}
	}

	private List<Pair<NodeKey, Map<String, String>>> getOutgoingList(NodeKey source) {
		Map<String, Object> exampleEdge = new HashMap<>();
		exampleEdge.put("documentType", "edge");
		exampleEdge.put("graphId", graphId);
		exampleEdge.put("source", createOrGetKeyDocumentAndGetId(source));
		exampleEdge.put("isActive", true);
		List<Map<String, Object>> edgeList = documentDAO.find(exampleEdge);
		List<Pair<NodeKey, Map<String, String>>> result = new LinkedList<>();
		for (Map<String, Object> edge : edgeList) {
			NodeKey targetKey = getNodeKey(edge.get("target"));
			result.add(new Pair<>(targetKey, (Map<String, String>) edge.get("attr")));
		}
		return result;
	}

	private Object createOrGetKeyDocumentAndGetId(NodeKey key) {
		return createOrGetKeyDocumentAndGetId(key, true);
	}

	private Object createOrGetKeyDocumentAndGetId(NodeKey key, boolean lookForActive) {
		Map<String, Object> obj = new HashMap<String, Object>(key);
		obj.put("graphId", graphId);
		if (lookForActive) {
			obj.put("isActive", true);
		}

		Map<String, Object> found = documentDAO.findKey(obj);
		if (found == null) {
			documentDAO.saveKey(obj);
			return obj.get("_id");
		}
		return found.get("_id");
	}

	private void deleteNodeKey(Object nodeKeyRaw) {
		Map<String, Object> example = new HashMap<>();
		example.put("_id", nodeKeyRaw);
		example.put("graphId", graphId);
		documentDAO.deleteKey(example);
	}

	private NodeKey getNodeKey(Object nodeKeyRaw) {
		return getNodeKey(nodeKeyRaw, true);
	}

	private NodeKey getNodeKey(Object nodeKeyRaw, boolean lookForActive) {
		Map<String, Object> example = new HashMap<>();
		example.put("_id", nodeKeyRaw);
		if (lookForActive) {
			example.put("isActive", true);
		}
		Map<String, Object> foundKey = documentDAO.findKey(example);

		NodeKey nodeKey = new NodeKey(foundKey.get("type").toString());
		for (String s : foundKey.keySet()) {
			nodeKey.put(s, foundKey.get(s).toString());
		}
		//clear potentially dangerous and unnecessary fields
		nodeKey.remove("_id");
		nodeKey.remove("graphId");
		nodeKey.remove("documentType");
		nodeKey.remove("isActive");
		nodeKey.remove("version");
		return nodeKey;
	}

	private void changeGraphDocumentState(String field, Object val) {
		Map<String, Object> graphDocument = getGraphDocument();
		Map<String, Object> next = copyDocument(graphDocument);
		next.put(field, val);
		documentDAO.update(graphDocument, next);
		logger.debug("Graph " + graphId + " document state changed for " + field + " from " + graphDocument.get(field) + " to " + val);
	}

	private int currentVersion() {
		return Integer.parseInt(getGraphDocument().get("currentVersion").toString());
	}

	private void changeElementsStateByVersion(final int version, final boolean state) {
		changeEdgeStateByVersion(version, state);
		// find nodes for this version
		mapNodesByVersion(version, new DocumentVisitor() {

			@Override
			public void visit(Map<String, Object> dbObj) {
				Object key = dbObj.get("key");

				Map<String, Object> next = copyDocument(dbObj);
				next.put("isActive", state);
				documentDAO.update(dbObj, next);

				// also you have to change state of key container of the node
				BasicDBObject example = new BasicDBObject();
				example.put("_id", key);
				example.put("graphId", graphId);

				Map<String, Object> foundKey = documentDAO.findKey(example);
				Map<String, Object> keyCopy = copyDocument(foundKey);

				keyCopy.put("isActive", state);
				documentDAO.updateKey(foundKey, keyCopy);
			}
		});
	}

	private void mapNodesByVersion(int version, DocumentVisitor visitor) {
		Map<String, Object> document = new HashMap<>();
		document.put("graphId", graphId);
		document.put("documentType", "node");
		//		document.put("isActive", true);
		document.put("version", version);
		List<Map<String, Object>> documentList = documentDAO.find(document);
		for (Map<String, Object> d : documentList) {
			visitor.visit(d);
		}
	}

	private void mapNodesByKey(NodeKey key, DocumentVisitor visitor) {
		Object oid = createOrGetKeyDocumentAndGetId(key);
		Map<String, Object> document = new HashMap<>();
		document.put("key", oid);
		Map<String, Object> one = documentDAO.findOne(document);
		visitor.visit(one);
	}

	private Map<String, Object> copyDocument(Map<String, Object> doc) {
		Map<String, Object> newObj = new HashMap<>();
		for (Map.Entry<String, Object> entry : doc.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof BasicDBList) {
				BasicDBList n = (BasicDBList) value;
				newObj.put(key, n.copy());
			} else {
				newObj.put(key, value);
			}
		}
		return newObj;
	}

	@Override
	public List<GraphNode> nodes() {
		Map<String, Object> document = new HashMap<>();
		document.put("graphId", graphId);
		document.put("documentType", "node");
		document.put("isActive", true);

		List<Map<String, Object>> result = documentDAO.find(document);

		List<GraphNode> nodes = new LinkedList<>();
		for (Map<String, Object> next : result) {
			Object nodeKeyRaw = next.get("key");

			NodeKey nodeKey = getNodeKey(nodeKeyRaw);
			GraphNode node = new GraphNodeProxyImpl(nodeKey, this, false);

			nodes.add(node);
		}
		return nodes;
	}

	interface DocumentVisitor {

		void visit(Map<String, Object> obj);

	}

}