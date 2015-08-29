package org.test.gserver.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.test.gserver.GraphEdge;
import org.test.gserver.GraphLockTimeOutException;
import org.test.gserver.GraphNode;
import org.test.gserver.GraphStorage;
import org.test.gserver.NodeKey;
import org.test.gserver.internal.nosql.DocumentDAO;
import org.test.gserver.internal.nosql.MongoDAO;

import java.util.List;
import java.util.Map;

/**
 * This is a decorator class to add locking support to {@link org.test.gserver.internal.DocumentStorage}.
 *
 * @author serkan
 */
class DocumentStorageLockSupport implements GraphStorage {

	private Logger logger = LoggerFactory.getLogger(DocumentStorageLockSupport.class);

	private DocumentDAO documentDAO = new MongoDAO();

	private GraphStorage storage;

	private LockManager lockManager;

	public DocumentStorageLockSupport(String graphId) {
		storage = new DocumentStorage(graphId);
		lockManager = new LockManagerImpl(storage);
	}

	@Override
	public void createNodeIfNotExist(NodeKey key) {
		try {
			lockManager.lock(key);
		} catch (GraphLockTimeOutException e) {
			throw new RuntimeException(e);
		}
		storage.createNodeIfNotExist(key);
		lockManager.release(key);
	}

	@Override
	public List<GraphNode> roots() {
		// TODO (serkan) add isRoot flag while creating nodes and when neighbors added set the flag false
		// TODO (serkan) retrieve only rootFlag=true nodes
		return null;
	}

	@Override
	public void removeNode(NodeKey key) {
		storage.removeNode(key);
	}

	@Override
	public void addNeighbor(NodeKey sourceKey, GraphNode target, Map<String, String> attr) {
		storage.addNeighbor(sourceKey, target, attr);
	}

	@Override
	public List<GraphEdge> getNeighbors(NodeKey key) {
		return storage.getNeighbors(key);
	}

	@Override
	public void putAttr(NodeKey key, Map<String, String> attr) {
		try {
			lockManager.lock(key);
		} catch (GraphLockTimeOutException e) {
			throw new RuntimeException(e);
		}
		storage.putAttr(key, attr);
		lockManager.release(key);
	}

	@Override
	public Map<String, String> getAttr(NodeKey key) {
		return storage.getAttr(key);
	}

	@Override
	public void removeAll() {
		storage.removeAll();
	}

	@Override
	public int nodesSize() {
		return storage.nodesSize();
	}

	@Override
	public boolean atomicLock(String owner, NodeKey key, long maxLockTime) {
		return storage.atomicLock(owner, key, maxLockTime);
	}

	@Override
	public void releaseLock(String owner, NodeKey key) {
		storage.releaseLock(owner, key);
	}

	@Override
	public List<GraphEdge> edges() {
		return storage.edges();
	}

	@Override
	public List<GraphEdge> getEdges(NodeKey source, NodeKey target) {
		return storage.getEdges(source, target);
	}

	@Override
	public void changeTxFlagAs(boolean flag) {
		throw new IllegalStateException("Not applicable in locking mode");
	}

	@Override
	public boolean getTxFlag() {
		throw new IllegalStateException("Not applicable in locking mode");
	}

	@Override
	public int increaseCurrentVersion() {
		throw new IllegalStateException("Not applicable in locking mode");
	}

	@Override
	public int decreaseCurrentVersion() {
		throw new IllegalStateException("Not applicable in locking mode");
	}

	@Override
	public int getCurrentVersion() {
		return currentVersion();
	}

	@Override
	public int getLastVersion() {
		throw new IllegalStateException("Not applicable in locking mode");
	}

	@Override
	public void changeLastVersionAs(int version) {
		throw new IllegalStateException("Not applicable in locking mode");
	}

	@Override
	public void removeElementsByVersion(int version) {
		throw new IllegalStateException("Not applicable in locking mode");
	}

	@Override
	public void inactivateElementsByVersion(int version) {
		throw new IllegalStateException("Not applicable in locking mode");
	}

	@Override
	public void activateElementsByVersion(int version) {
		throw new IllegalStateException("Not applicable in locking mode");
	}

	@Override
	public void inactivateElementsByNode(final NodeKey key) {
		throw new IllegalStateException("Not applicable in locking mode");
	}

	private int currentVersion() {
		throw new IllegalStateException("Not applicable in locking mode");
	}

	@Override
	public List<GraphNode> nodes() {
		return storage.nodes();
	}

}