package org.test.gserver;

import java.util.List;
import java.util.Map;

/**
 * Data recording abstraction to keep data of nodes and edges,
 * depending on implementation it can store graph  in memory,
 * NoSql-db, relational-db or simply in a file-system.
 * <p/>
 * <b>Please note the comments of methods before implementing
 * this interface.</b>
 *
 * @author serkan
 */
public interface GraphStorage {

    /**
     * All nodes in the graph. It can be used to iterate all at once,
     * too costly something like depth-first search or breadth first search.
     * Graph representations normally do not provide this functionality
     * directly, here this can be a beneficial feature because of storage
     * abstraction.
     *
     * @return List of {@link org.test.gserver.GraphNode}
     */
    List<GraphNode> nodes();

    /**
     * Only nodes with no incoming edges. It can be traversed
     * all graph through neighbors.
     *
     * @return List of {@link org.test.gserver.GraphNode}
     */
    List<GraphNode> roots();

    /**
     * Removes a node from storage.
     * Implementer must delete all incoming edges to this node too.
     */
    void removeNode(NodeKey key);

    /**
     * Creates a node for given id and type in storage.
     */
    void createNodeIfNotExist(NodeKey key);

    /**
     * As default adds neighbors directed from given source to given
     * target.
     *
     * @param target target graph node
     */
    void addNeighbor(NodeKey key, GraphNode target, Map<String, String> attr);

    /**
     * Return list of outgoing nodes from given source node.
     *
     * @return
     */
    List<GraphEdge> getNeighbors(NodeKey key);

    /**
     * Adds attributes to given node.
     *
     * @param attr attribute list as key-value pairs {@link java.util.Map}
     */
    void putAttr(NodeKey key, Map<String, String> attr);

    /**
     * Returns attributes of given node.
     *
     * @return list of key-value pairs
     */
    Map<String, String> getAttr(NodeKey key);

    int nodesSize();

    boolean atomicLock(String owner, NodeKey key, long maxLockTime);

    void releaseLock(String owner, NodeKey key);

    List<GraphEdge> edges();

    List<GraphEdge> getEdges(NodeKey source, NodeKey target);

    void markCheckPoint();

    void undo();

    void redo();

}
