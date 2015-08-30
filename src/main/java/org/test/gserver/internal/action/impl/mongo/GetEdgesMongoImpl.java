package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.GraphEdge;
import org.test.gserver.NodeKey;
import org.test.gserver.internal.action.GetEdgesAction;

import java.util.List;

/**
 * Created by serkan on 30.08.2015.
 */
public class GetEdgesMongoImpl implements GetEdgesAction {

    private NodeKey source;

    private NodeKey target;

    @Override
    public void configure(Object... params) {
        source = (NodeKey) params[0];
        target = (NodeKey) params[1];
    }

    @Override
    public List<GraphEdge> execute() {
        if (source == null
                || target == null) {
            throw new NullPointerException("Source and target keys must be given before execution");
        }
        return null;
    }

    @Override
    public void undo() {

    }
}
