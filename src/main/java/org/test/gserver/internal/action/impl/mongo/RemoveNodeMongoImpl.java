package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.NodeKey;
import org.test.gserver.internal.action.RemoveNodeAction;

/**
 * Created by serkan on 30.08.2015.
 */
public class RemoveNodeMongoImpl implements RemoveNodeAction {

    private NodeKey nodeKey;

    @Override
    public void configure(Object... params) {
        nodeKey = (NodeKey) params[0];
    }

    @Override
    public Void execute() {
        if (nodeKey == null) {
            throw new NullPointerException("NodeKey must be given with configure before execution");
        }
        return null;
    }

    @Override
    public void undo() {

    }
}
