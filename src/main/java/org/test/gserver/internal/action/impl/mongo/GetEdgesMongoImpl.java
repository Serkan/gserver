package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.GraphEdge;
import org.test.gserver.GraphNode;
import org.test.gserver.GraphStorage;
import org.test.gserver.NodeKey;
import org.test.gserver.internal.GraphNodeProxyImpl;
import org.test.gserver.internal.action.GetEdgesAction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by serkan on 30.08.2015.
 */
public class GetEdgesMongoImpl extends AbstractMongoAction implements GetEdgesAction {

    private NodeKey source;
    private NodeKey target;
    private GraphStorage storage;

    @Override
    public void configure(Object... params) {
        loadGraphIdFromParams(params);
        source = (NodeKey) params[1];
        target = (NodeKey) params[2];
        storage = (GraphStorage) params[3];
    }

    @Override
    public List<GraphEdge> execute() {
        if (source == null
                || target == null) {
            throw new NullPointerException("Source and target keys must be given before execution");
        }
        Object sourceObjId = createOrGetKeyDocumentAndGetId(source);
        Object targetObjId = createOrGetKeyDocumentAndGetId(target);

        Map<String, Object> edgeExample = new HashMap<>();
        edgeExample.put("documentType", "edge");
        edgeExample.put("graphId", getGraphId());
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

            GraphNode sourceNode = new GraphNodeProxyImpl(sourceKey, storage, false);
            GraphNode targetNode = new GraphNodeProxyImpl(targetKey, storage, false);

            GraphEdge edge = new GraphEdge(sourceNode, targetNode, (Map<String, String>) attr);
            result.add(edge);
        }
        return result;
    }

    @Override
    public void undo() {
        // do nothing
    }
}
