package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.NodeKey;
import org.test.gserver.internal.action.PutAttrAction;

import java.util.Map;

/**
 * Created by serkan on 3class0.08.2015.
 */
public class PutAttrMongoImpl implements PutAttrAction {

    private NodeKey nodeKey;

    private Map<String, String> attr;

    @Override
    public void configure(Object... params) {
        nodeKey = (NodeKey) params[0];
        attr = (Map<String, String>) params[1];
    }

    @Override
    public Void execute() {
        if (nodeKey == null
                || attr == null) {
            throw new NullPointerException("None of the parameters can not be null");
        }
        return null;
    }

    @Override
    public void undo() {

    }
}
