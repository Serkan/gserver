package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.GraphEdge;
import org.test.gserver.GraphNode;
import org.test.gserver.NodeKey;
import org.test.gserver.internal.GraphNodeProxyImpl;
import org.test.gserver.internal.action.GetAllEdgesAction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by serkan on 30.08.2015.
 */
public class GetAllEdgesMongoImpl extends AbstractMongoAction implements GetAllEdgesAction {

    private String graphId;

    @Override
    public void configure(Object... params) {
        loadGraphIdFromParams(params);
        graphId = (String) params[1];
    }

    @Override
    public List<GraphEdge> execute() {
        if (graphId == null) {
            throw new NullPointerException("GraphId can not be null before execution.");
        }
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
    public void undo() {
        // do nothing
    }
}
