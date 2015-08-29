package org.test.gserver.internal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.test.gserver.GraphLockTimeOutException;
import org.test.gserver.NodeKey;
import org.test.gserver.Pair;

import java.util.UUID;

public class LockManagerTest {

	@Before
	public void setUp() throws Exception {
		// TODO clear locks
	}

	@After
	public void tearDown() throws Exception {
		// TODO clear locks
	}

	@Test(expected = GraphLockTimeOutException.class)
	public void testAcquireLockTimeOut() throws GraphLockTimeOutException {
		NodeKey key = new NodeKey("TYPE_P", new Pair<>("id", "1111111111"));

		String graphId = UUID.randomUUID().toString();

		LockManager lockManager = new LockManagerImpl(new DocumentStorageLockSupport(graphId));
		lockManager.lock(key);

		lockManager = new LockManagerImpl(new DocumentStorageLockSupport(graphId), new LockOwnerProvider() {

			@Override
			public String getOwner() {
				return "anonymous" + System.currentTimeMillis();
			}
		});
		lockManager.lock(key);
	}

	@Test
	public void testAcquireLock() throws GraphLockTimeOutException {
		NodeKey key = new NodeKey("TYPE_P", new Pair<>("id", "1111111111"));

		String graphId = UUID.randomUUID().toString();

		LockManager lockManager = new LockManagerImpl(new DocumentStorageLockSupport(graphId));
		lockManager.lock(key);
		// do some work and release lock
		lockManager.release(key);

		// different lock manager to acquire lock as different owner
		lockManager = new LockManagerImpl(new DocumentStorageLockSupport(graphId), new LockOwnerProvider() {

			@Override
			public String getOwner() {
				return "anonymous" + System.currentTimeMillis();
			}
		});
		lockManager.lock(key);
		lockManager.release(key);
	}

	@Test
	public void testAcquireExpiredLock() throws GraphLockTimeOutException, InterruptedException {
		// lock a node but dont release
		NodeKey key = new NodeKey("TYPE_P", new Pair<>("id", "1111111111"));
		String graphId = UUID.randomUUID().toString();
		{
			LockManager lockManager = new LockManagerImpl(new DocumentStorageLockSupport(graphId));
			lockManager.lock(key, 1);
		}
		// wait it to expire
		Thread.currentThread().sleep(1);
		// try acquire lock
		{
			LockManager lockManager = new LockManagerImpl(new DocumentStorageLockSupport(graphId), new LockOwnerProvider() {

				@Override
				public String getOwner() {
					return "anonymous" + System.currentTimeMillis();
				}
			});
			lockManager.lock(key);
			lockManager.release(key);
		}
	}

}