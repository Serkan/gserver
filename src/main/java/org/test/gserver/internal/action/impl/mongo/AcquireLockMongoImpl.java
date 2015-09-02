package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.NodeKey;
import org.test.gserver.internal.action.AcquireLockAction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sturgut on 9/2/15.
 */
public class AcquireLockMongoImpl extends AbstractMongoAction implements AcquireLockAction {

    private String owner;
    private NodeKey nodeKey;
    private Long maxLongTimeInNano;

    @Override
    public void configure(Object... params) {
        loadGraphIdFromParams(params);
        owner = (String) params[1];
        nodeKey = (NodeKey) params[2];
        maxLongTimeInNano = (Long) params[3];
    }

    @Override
    public Boolean execute() {
        Map<String, Object> lockDoc = new HashMap<>();
        lockDoc.put("graphId", getGraphId());
        lockDoc.put("key", nodeKey);
        lockDoc.put("owner", owner);
        // set expire time 'maxLockTime' milliseconds after its created
        lockDoc.put("expires", System.nanoTime() + maxLongTimeInNano);

        Map<String, Object> queryDoc = new HashMap<>();
        queryDoc.put("graphId", getGraphId());
        queryDoc.put("key", nodeKey);
        // set expire time 'maxLockTime' milliseconds after its created
        queryDoc.put("expires", System.nanoTime());

        return documentDAO.upsertLock(lockDoc, queryDoc);
    }

    @Override
    public void undo() {

    }
}
