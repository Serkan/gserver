package org.test.gserver.internal;

import org.test.gserver.GraphLockTimeOutException;
import org.test.gserver.NodeKey;

/**
 *
 * @author serkan
 */
public interface LockManager {

	void lock(NodeKey key, long maxLockTime) throws GraphLockTimeOutException;

	void lock(NodeKey key) throws GraphLockTimeOutException;

	void release(NodeKey key);

}
