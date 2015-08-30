package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.GraphEdge;
import org.test.gserver.NodeKey;
import org.test.gserver.internal.action.GetNeighborsAction;

import java.util.List;

/**
 * Created by serkan on 30.08.2015.
 */
public class GetNeighborsMongoImpl implements GetNeighborsAction {

    private NodeKey nodeKey;

    @Override
    public void configure(Object... params) {
        nodeKey = (NodeKey) params[0];
    }

    @Override
    public List<GraphEdge> execute() {
        if (nodeKey == null) {
            throw new NullPointerException("NodeKey can not be null before execution.");
        }
        return null;
    }

    @Override
    public void undo() {

    }
}
