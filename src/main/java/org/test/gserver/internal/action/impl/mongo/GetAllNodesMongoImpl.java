package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.GraphNode;
import org.test.gserver.GraphStorage;
import org.test.gserver.NodeKey;
import org.test.gserver.internal.GraphNodeProxyImpl;
import org.test.gserver.internal.action.GetAllNodesAction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by serkan on 30.08.2015.
 */
public class GetAllNodesMongoImpl extends AbstractMongoAction implements GetAllNodesAction {

    private GraphStorage storage;

    @Override
    public void configure(Object... params) {
        loadGraphIdFromParams(params);
        storage = (GraphStorage) params[1];
    }

    @Override
    public List<GraphNode> execute() {
        Map<String, Object> document = new HashMap<>();
        document.put("graphId", getGraphId());
        document.put("documentType", "node");
        document.put("isActive", true);

        List<Map<String, Object>> result = documentDAO.find(document);

        List<GraphNode> nodes = new LinkedList<>();
        for (Map<String, Object> next : result) {
            Object nodeKeyRaw = next.get("key");

            NodeKey nodeKey = getNodeKey(nodeKeyRaw);
            GraphNode node = new GraphNodeProxyImpl(nodeKey, storage, false);

            nodes.add(node);
        }
        return nodes;
    }

    @Override
    public void undo() {
        // do nothing
    }
}
