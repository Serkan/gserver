package org.test.gserver.internal;

import com.google.gson.Gson;
import com.mongodb.*;
import org.test.gserver.GraphEdge;
import org.test.gserver.GraphNode;
import org.test.gserver.GraphStorage;
import org.test.gserver.NodeKey;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class MongoStorage implements GraphStorage {

    private static final String HOST = "localhost";

    private static final int PORT = 27017;

    private final DBCollection graph;

    private MongoClient mongoClient;

    private final String graphId;

    public MongoStorage(String graphId) {
        try {
            mongoClient = new MongoClient(HOST, PORT);
            graph = mongoClient.getDB("graph").getCollection("visia");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        this.graphId = graphId;
        // ensure indexes
        BasicDBObject document = new BasicDBObject();
        document.put("graphId", 1);
        document.put("key", 1);
        graph.createIndex(document);
    }

    @Override
    public void createNodeIfNotExist(NodeKey key) {
        BasicDBObject document = new BasicDBObject();
        document.put("graphId", graphId);
        document.put("documentType", "node");
        Object oID = createOrGetKeyDocumentAndGetId(key);
        document.put("key", oID);
        if (graph.count(document) < 1) {
            graph.save(document);
        }
    }

    @Override
    public void addNeighbor(NodeKey sourceKey, GraphNode target, Map<String, String> attr) {
        BasicDBObject document = new BasicDBObject();
        document.put("graphId", graphId);
        Object sourceOID = createOrGetKeyDocumentAndGetId(sourceKey);
        document.put("key", sourceOID);
        DBObject node = graph.findOne(document);

        BasicDBList neighborList;
        BasicDBObject edge = new BasicDBObject();
        Object oID = createOrGetKeyDocumentAndGetId(target.getKey());
        edge.put("key", oID);
        edge.put("attr", attr);

        BasicDBObject old = (BasicDBObject) node;
        BasicDBObject next = (BasicDBObject) old.copy();
        Object neighbors = next.get("neighbors");

        if (neighbors != null) {
            neighborList = (BasicDBList) neighbors;
            if (neighborList.contains(edge)) {
                return;
            }
        } else {
            neighborList = new BasicDBList();
        }

        neighborList.add(edge);
        next.put("neighbors", neighborList);
        graph.update(old, next);
    }

    @Override
    public List<GraphEdge> getNeighbors(NodeKey key) {
        BasicDBObject document = new BasicDBObject();
        document.put("graphId", graphId);
        Object oID = createOrGetKeyDocumentAndGetId(key);
        document.put("key", oID);
        DBObject node = graph.findOne(document);

        BasicDBList neighborList;
        Object neighbors = node.get("neighbors");

        if (neighbors != null) {
            neighborList = (BasicDBList) neighbors;
            List<GraphEdge> result = new LinkedList<>();
            for (Object o : neighborList) {
                BasicDBObject obj = (BasicDBObject) o;
                Object nodeKeyRaw = obj.get("key"); // convert json to map
                BasicDBObject edgeAttrRaw = (BasicDBObject) obj.get("attr");// convert json to map

                NodeKey nodeKey = getNodeKey(nodeKeyRaw);

                Map<String, String> edgeAttr = new HashMap<>();
                if (edgeAttrRaw != null && edgeAttrRaw.size() > 0) {
                    for (String s : edgeAttrRaw.keySet()) {
                        edgeAttr.put(s, edgeAttrRaw.get(s).toString());
                    }
                }

                GraphNode targetNode = new GraphNodeProxyImpl(nodeKey, this);
                GraphEdge edge = new GraphEdge(targetNode, edgeAttr);
                result.add(edge);
            }
            return result;
        } else {
            return null;
        }
    }

    @Override
    public void removeNode(NodeKey key) {
        BasicDBObject document = new BasicDBObject();
        document.put("graphId", graphId);
        Object oID = createOrGetKeyDocumentAndGetId(key);
        document.put("key", oID);
        graph.remove(document);
    }

    @Override
    public void putAttr(NodeKey key, Map<String, String> attr) {
        BasicDBObject document = new BasicDBObject();
        document.put("graphId", graphId);
        Object oID = createOrGetKeyDocumentAndGetId(key);
        document.put("key", oID);
        BasicDBObject old = (BasicDBObject) graph.findOne(document);
        BasicDBObject next = (BasicDBObject) old.copy();
        next.put("attr", attr);
        graph.update(old, next);
    }

    @Override
    public Map<String, String> getAttr(NodeKey key) {
        BasicDBObject document = new BasicDBObject();
        document.put("graphId", graphId);
        Object oID = createOrGetKeyDocumentAndGetId(key);
        document.put("key", oID);
        DBObject node = graph.findOne(document);
        return new Gson().fromJson(node.get("attr").toString(), HashMap.class);
    }

    @Override
    public List<GraphNode> nodes() {
        BasicDBObject document = new BasicDBObject();
        document.put("graphId", graphId);
        document.put("documentType", "node");
        DBCursor cursor = graph.find(document);
        List<GraphNode> nodes = new LinkedList<>();
        while (cursor.hasNext()) {
            DBObject next = cursor.next();
            Object nodeKeyRaw = next.get("key");
            NodeKey nodeKey = getNodeKey(nodeKeyRaw);
            GraphNode node = new GraphNodeProxyImpl(nodeKey, this);
            nodes.add(node);
        }
        return nodes;
    }

    @Override
    public List<GraphNode> roots() {
        // TODO (serkan) add isRoot flag while creating nodes and when neighbors added set the flag false
        // TODO (serkan) retrieve only rootFlag=true nodes
        return null;
    }

    private Object createOrGetKeyDocumentAndGetId(NodeKey key) {
        BasicDBObject obj = new BasicDBObject(key);
        obj.put("graphId", graphId);
        DBObject found = graph.findOne(obj);
        if (found == null) {
            graph.save(obj);
            return obj.get("_id");
        }
        return found.get("_id");
    }

    private NodeKey getNodeKey(Object nodeKeyRaw) {
        BasicDBObject example = new BasicDBObject();
        example.put("_id", nodeKeyRaw);
        example.put("graphId", graphId);
        DBObject foundKey = graph.findOne(example);

        NodeKey nodeKey = new NodeKey(foundKey.get("type").toString());
        for (String s : foundKey.keySet()) {
            nodeKey.put(s, foundKey.get(s).toString());
        }
        nodeKey.remove("_id");
        nodeKey.remove("graphId");
        return nodeKey;
    }


}