package org.test.gserver.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.test.gserver.GraphAction;
import org.test.gserver.GraphLockTimeOutException;
import org.test.gserver.NodeKey;
import org.test.gserver.internal.locking.LockManagerImpl;

import java.util.Map;

/**
 * @author serkan
 */
class GraphStorageLockSupport extends AbstractGraphStorage {

    private Logger logger = LoggerFactory.getLogger(GraphStorageLockSupport.class);
    private LockManager lockManager;

    public GraphStorageLockSupport(String graphId, GraphActionFactory actionFactory) {
        super(graphId, actionFactory);
        lockManager = new LockManagerImpl(this);
    }

    @Override
    protected <T> T delegate(ActionType actionType, Object... params) {
        GraphAction<T> action = lookup(actionType);
        Object[] fullParams = new Object[params.length + 1];
        fullParams[0] = getGraphId();
        System.arraycopy(params, 0, fullParams, 1, params.length);
        action.configure(fullParams);
        return action.execute();
    }

    @Override
    public void createNodeIfNotExist(NodeKey key) {
        try {
            lockManager.lock(key);
        } catch (GraphLockTimeOutException e) {
            throw new RuntimeException(e);
        }
        this.createNodeIfNotExist(key);
        lockManager.release(key);
    }

    @Override
    public void putAttr(NodeKey key, Map<String, String> attr) {
        try {
            lockManager.lock(key);
        } catch (GraphLockTimeOutException e) {
            throw new RuntimeException(e);
        }
        this.putAttr(key, attr);
        lockManager.release(key);
    }

    @Override
    public boolean atomicLock(String owner, NodeKey key, long maxLockTime) {
        return delegate(ActionType.ACQUIRE_LOCK, owner, key, maxLockTime);
    }

    @Override
    public void releaseLock(String owner, NodeKey key) {
        delegate(ActionType.RELEASE_LOCK, owner, key);
    }

    @Override
    public void markCheckPoint() {
        throw new UnsupportedOperationException("Unsupported in locking mode");
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Unsupported in locking mode");
    }

    @Override
    public void redo() {
        throw new UnsupportedOperationException("Unsupported in locking mode");
    }

}