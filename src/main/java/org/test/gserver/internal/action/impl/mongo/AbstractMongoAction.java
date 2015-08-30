package org.test.gserver.internal.action.impl.mongo;

import com.mongodb.BasicDBList;
import org.test.gserver.NodeKey;
import org.test.gserver.Pair;
import org.test.gserver.internal.nosql.DocumentDAO;
import org.test.gserver.internal.nosql.MongoDAO;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by serkan on 30.08.2015.
 */
public abstract class AbstractMongoAction {

    private String graphId;

    protected DocumentDAO documentDAO = new MongoDAO();


    protected void loadGraphIdFromParams(Object... params) {
        graphId = (String) params[0];
    }

    protected String getGraphId() {
        return graphId;
    }

    protected void createEdge(NodeKey source, NodeKey target, Map<String, String> attr) {
        Map<String, Object> edge = new HashMap<>();
        edge.put("documentType", "edge");
        edge.put("graphId", graphId);
        edge.put("source", createOrGetKeyDocumentAndGetId(source));
        edge.put("target", createOrGetKeyDocumentAndGetId(target));
        edge.put("attr", attr);
        edge.put("isActive", true);
        documentDAO.save(edge);
    }

    protected void removeOutgoingEdges(NodeKey source) {
        Map<String, Object> edge = new HashMap<>();
        edge.put("documentType", "edge");
        edge.put("graphId", graphId);
        edge.put("source", createOrGetKeyDocumentAndGetId(source));
        edge.put("isActive", true);
        documentDAO.delete(edge);
    }

    protected void removeIncomingEdges(NodeKey target) {
        Map<String, Object> edge = new HashMap<>();
        edge.put("documentType", "edge");
        edge.put("graphId", graphId);
        edge.put("target", createOrGetKeyDocumentAndGetId(target));
        edge.put("isActive", true);
        documentDAO.delete(edge);
    }

    protected boolean existEdge(NodeKey source, NodeKey target, Map<String, String> attr) {
        Map<String, Object> edge = new HashMap<>();
        edge.put("documentType", "edge");
        edge.put("graphId", graphId);
        edge.put("source", createOrGetKeyDocumentAndGetId(source));
        edge.put("target", createOrGetKeyDocumentAndGetId(target));
        edge.put("attr", attr);
        edge.put("isActive", true);
        return documentDAO.count(edge) > 0;
    }

    protected List<Pair<NodeKey, Map<String, String>>> getOutgoingList(NodeKey source) {
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

    protected Object createOrGetKeyDocumentAndGetId(NodeKey key) {
        return createOrGetKeyDocumentAndGetId(key, true);
    }

    protected Object createOrGetKeyDocumentAndGetId(NodeKey key, boolean lookForActive) {
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

    protected void deleteNodeKey(Object nodeKeyRaw) {
        Map<String, Object> example = new HashMap<>();
        example.put("_id", nodeKeyRaw);
        example.put("graphId", graphId);
        documentDAO.deleteKey(example);
    }

    protected NodeKey getNodeKey(Object nodeKeyRaw) {
        return getNodeKey(nodeKeyRaw, true);
    }

    protected NodeKey getNodeKey(Object nodeKeyRaw, boolean lookForActive) {
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

    protected Map<String, Object> copyDocument(Map<String, Object> doc) {
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

    protected void createGraph() {
        Map<String, Object> document = new HashMap<>();
        document.put("graphId", graphId);
        document.put("documentType", "header");
        document.put("currentVersion", 0);
        document.put("lastVersion", 0);
        document.put("txFlag", false);
        documentDAO.save(document);
    }

    protected boolean graphExists() {
        Map<String, Object> document = new HashMap<>();
        document.put("graphId", graphId);
        document.put("documentType", "header");
        return documentDAO.count(document) != 0;
    }

}
