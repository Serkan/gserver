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
    public int nodesSize() {
        Map<String, Object> document = new HashMap<>();
        document.put("graphId", graphId);
        document.put("isActive", true);
        return (int) documentDAO.countKeys(document);
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

}