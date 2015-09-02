package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.*;
import org.test.gserver.internal.GraphNodeProxyImpl;
import org.test.gserver.internal.action.GetNeighborsAction;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by serkan on 30.08.2015.
 */
public class GetNeighborsMongoImpl extends AbstractMongoAction implements GetNeighborsAction {

    private NodeKey nodeKey;

    private GraphStorage storage;

    @Override
    public void configure(Object... params) {
        loadGraphIdFromParams(params);
        nodeKey = (NodeKey) params[1];
        storage = (GraphStorage) params[2];
    }

    @Override
    public List<GraphEdge> execute() {
        if (nodeKey == null) {
            throw new NullPointerException("NodeKey can not be null before execution.");
        }
        List<Pair<NodeKey, Map<String, String>>> outgoingList = getOutgoingList(nodeKey);
        if (outgoingList.size() != 0) {
            List<GraphEdge> result = new LinkedList<>();
            for (Pair<NodeKey, Map<String, String>> nodeKeyMapPair : outgoingList) {
                GraphNode targetNode = new GraphNodeProxyImpl(nodeKeyMapPair.getFirst(), storage, false);
                GraphNode sourceNode = new GraphNodeProxyImpl(nodeKey, storage, false);
                GraphEdge edge = new GraphEdge(sourceNode, targetNode, nodeKeyMapPair.getSecond());
                result.add(edge);
            }
            return result;
        } else {
            return null;
        }
    }

    @Override
    public void undo() {
        // do nothing
    }
}
