package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.GraphEdge;
import org.test.gserver.GraphNode;
import org.test.gserver.GraphStorage;
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

    private GraphStorage storage;

    @Override
    public void configure(Object... params) {
        loadGraphIdFromParams(params);
        storage = (GraphStorage) params[1];
    }

    @Override
    public List<GraphEdge> execute() {
        Map<String, Object> edgeExample = new HashMap<>();
        edgeExample.put("documentType", "edge");
        edgeExample.put("graphId", getGraphId());
        List<Map<String, Object>> edges = documentDAO.find(edgeExample);

        List<GraphEdge> result = new LinkedList<>();
        for (Map<String, Object> edgeMap : edges) {
            NodeKey sourceKey = getNodeKey(edgeMap.get("source"));
            NodeKey targetKey = getNodeKey(edgeMap.get("target"));
            Object attr = edgeMap.get("attr");

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
