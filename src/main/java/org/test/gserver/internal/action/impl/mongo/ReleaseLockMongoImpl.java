package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.NodeKey;
import org.test.gserver.internal.action.ReleaseLockAction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sturgut on 9/2/15.
 */
public class ReleaseLockMongoImpl extends AbstractMongoAction implements ReleaseLockAction {


    private NodeKey nodeKey;
    private String owner;

    @Override
    public void configure(Object... params) {
        loadGraphIdFromParams(params);
        owner = (String) params[1];
        nodeKey = (NodeKey) params[2];
    }

    @Override
    public Void execute() {
        Map<String, Object> lockDoc = new HashMap<>();
        lockDoc.put("key", nodeKey);
        lockDoc.put("graphId", getGraphId());
        documentDAO.deleteLock(lockDoc);
        return null;
    }

    @Override
    public void undo() {
        // nothing to do
    }
}
