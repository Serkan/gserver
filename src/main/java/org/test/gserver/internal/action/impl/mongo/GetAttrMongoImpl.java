package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.NodeKey;
import org.test.gserver.internal.action.GetAttrAction;

import java.util.Map;

/**
 * Created by serkan on 30.08.2015.
 */
public class GetAttrMongoImpl implements GetAttrAction {

    private NodeKey nodeKey;

    @Override
    public void configure(Object... params) {
        nodeKey = (NodeKey) params[0];
    }

    @Override
    public Map<String, String> execute() {
        if (nodeKey == null) {
            throw new NullPointerException("NodeKey can not be null");
        }
        return null;
    }

    @Override
    public void undo() {

    }
}
