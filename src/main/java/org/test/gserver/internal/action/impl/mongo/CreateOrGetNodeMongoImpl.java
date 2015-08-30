package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.GraphNode;
import org.test.gserver.NodeKey;
import org.test.gserver.internal.action.CreateOrGetNodeAction;

/**
 * Created by serkan on 30.08.2015.
 */
public class CreateOrGetNodeMongoImpl implements CreateOrGetNodeAction {

    private NodeKey nodeKey;

    @Override
    public void configure(Object... params) {
        nodeKey = (NodeKey) params[0];
    }

    @Override
    public GraphNode execute() {
        if (nodeKey == null) {
            throw new NullPointerException("NodeKey must be given with " +
                    "configure method before the execution");
        }
        return null;
    }

    @Override
    public void undo() {

    }
}
