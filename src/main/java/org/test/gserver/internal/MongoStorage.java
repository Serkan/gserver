package org.test.gserver.internal;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.*;
import org.test.gserver.GraphNode;
import org.test.gserver.GraphStorage;

import java.lang.reflect.Type;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by s_turgut_msk on 10/31/2014.
 */
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
        document.put("id", 1);
        document.put("type", 1);
        graph.createIndex(document);
    }

    @Override
    public void createNode(String id, String type) {
        BasicDBObject document = new BasicDBObject();
        document.put("graphId", graphId);
        document.put("id", id);
        document.put("type", type);
        if (graph.count(document) < 1) {
            graph.save(document);
        }
    }

    @Override
    public void addNeighbor(String id, String type, GraphNode target) {
        BasicDBObject document = new BasicDBObject();
        document.put("graphId", graphId);
        document.put("id", id);
        document.put("type", type);
        DBObject node = graph.findOne(document);

        List<NodeHead> neighborList = null;
        Type token = new TypeToken<ArrayList<NodeHead>>() {

        }.getType();
        Object neighbors = node.get("neighbors");
        Gson gson = new Gson();
        if (neighbors != null) {
            neighborList = gson.fromJson(neighbors.toString(), token);
            if (neighborList.contains(new NodeHead(target.getId(), target.getType()))) {
                return;
            }
        } else {
            neighborList = new ArrayList<>();
        }

        BasicDBObject old = (BasicDBObject) node;
        BasicDBObject next = (BasicDBObject) old.copy();
        neighborList.add(new NodeHead(target.getId(), target.getType()));
        // TODO (serkan) find a way store without escaped double quote in mongo
        next.put("neighbors", gson.toJson(neighborList));
        graph.update(old, next);
    }

    @Override
    public List<GraphNode> getNeighbors(String id, String type) {
        BasicDBObject document = new BasicDBObject();
        document.put("graphId", graphId);
        document.put("id", id);
        document.put("type", type);
        DBObject one = graph.findOne(document);
        return getNeighborList(one);
    }

    private List<GraphNode> getNeighborList(DBObject one) {
        Type token = new TypeToken<ArrayList<GraphNodeProxyImpl>>() {

        }.getType();
        Gson gson = new Gson();
        Object neighbors = one.get("neighbors");
        if (neighbors != null) {
            return gson.fromJson(neighbors.toString(), token);
        } else {
            return null;
        }
    }

    @Override
    public void removeNode(String id, String type) {
        BasicDBObject document = new BasicDBObject();
        document.put("graphId", graphId);
        document.put("id", id);
        document.put("type", type);
        graph.remove(document);
    }

    @Override
    public void putAttr(String id, String type, Map<String, String> attr) {
        BasicDBObject document = new BasicDBObject();
        document.put("graphId", graphId);
        document.put("id", id);
        document.put("type", type);
        BasicDBObject old = (BasicDBObject) graph.findOne(document);
        BasicDBObject next = (BasicDBObject) old.copy();
        next.put("attr", attr);
        graph.update(old, next);
    }

    @Override
    public Map<String, String> getAttr(String id, String type) {
        BasicDBObject document = new BasicDBObject();
        document.put("graphId", graphId);
        document.put("id", id);
        document.put("type", type);
        DBObject node = graph.findOne(document);
        return new Gson().fromJson(node.get("attr").toString(), HashMap.class);
    }

    @Override
    public List<GraphNode> nodes() {
        BasicDBObject document = new BasicDBObject();
        document.put("graphId", graphId);
        DBCursor cursor = graph.find(document);
        List<GraphNode> nodes = new LinkedList<>();
        while (cursor.hasNext()) {
            DBObject next = cursor.next();
            GraphNode node = new GraphNodeProxyImpl(next.get("id").toString(), next.get("type").toString(), this);
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

}