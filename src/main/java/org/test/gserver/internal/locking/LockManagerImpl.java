package org.test.gserver.internal.locking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.test.gserver.GraphLockTimeOutException;
import org.test.gserver.GraphStorage;
import org.test.gserver.NodeKey;
import org.test.gserver.internal.LockManager;
import org.test.gserver.internal.LockOwnerProvider;

import java.util.concurrent.locks.LockSupport;

/**
 * @author serkan
 */
public class LockManagerImpl implements LockManager {

    private GraphStorage storage;

    private LockOwnerProvider lockOwnerProvider;

    private Logger logger = LoggerFactory.getLogger(LockManagerImpl.class);

    public LockManagerImpl(GraphStorage storage) {
        this(storage, new DefaultLockOwnerProvider());
    }

    public LockManagerImpl(GraphStorage storage, LockOwnerProvider lockOwnerProvider) {
        this.storage = storage;
        this.lockOwnerProvider = lockOwnerProvider;
    }

    @Override
    public void lock(NodeKey key) throws GraphLockTimeOutException {
        // default lock time 5 seconds
        lock(key, 5000000000l);
    }

    @Override
    public void lock(NodeKey key, long maxLockTimeInNano) throws GraphLockTimeOutException {
        String owner = lockOwnerProvider.getOwner();
        if (logger.isDebugEnabled()) {
            logger.debug(owner + " trying to get lock  on " + key.toString() + " for " + maxLockTimeInNano + " nanoseconds.");
        }
        int i = 0;
        while (!storage.atomicLock(owner, key, maxLockTimeInNano)) {
            if (i++ > 10) {
                throw new GraphLockTimeOutException(key.toString() + " Could not be locked by " + owner);
            }
            LockSupport.parkNanos(1000);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(owner + " locked " + key.toString() + " for " + maxLockTimeInNano + " nanoseconds.");
        }
    }

    @Override
    public void release(NodeKey key) {
        String owner = lockOwnerProvider.getOwner();
        storage.releaseLock(owner, key);
        if (logger.isDebugEnabled()) {
            logger.debug(owner + " released lock on " + key.toString());
        }
    }

}
