package org.test.gserver.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.test.gserver.*;
import org.test.gserver.internal.locking.LockManagerImpl;

import java.util.List;
import java.util.Map;

/**
 *
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
        action.configure(getGraphId(), params);
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
        return this.atomicLock(owner, key, maxLockTime);
    }

    @Override
    public void releaseLock(String owner, NodeKey key) {
        this.releaseLock(owner, key);
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