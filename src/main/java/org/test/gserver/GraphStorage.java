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
     *
     * @param id   id of node
     * @param type type if node
     */
    void removeNode(String id, String type);

    /**
     * Creates a node for given id and type in storage.
     *
     * @param id   id of node
     * @param type type of node
     */
    void createNode(String id, String type);

    /**
     * As default adds neighbors directed from given source to given
     * target.
     *
     * @param id     id of source node
     * @param type   type of source node
     * @param target target graph node
     */
    void addNeighbor(String id, String type, GraphNode target);

    /**
     * Return list of outgoing nodes from given source node.
     *
     * @param id   id of source node
     * @param type type of source node
     * @return
     */
    List<GraphNode> getNeighbors(String id, String type);

    /**
     * Adds attributes to given node.
     *
     * @param id   id of node
     * @param type type of node
     * @param attr attribute list as key-value pairs {@link java.util.Map}
     */
    void putAttr(String id, String type, Map<String, String> attr);

    /**
     * Returns attributes of given node.
     *
     * @param id   id of node
     * @param type type of node
     * @return list of key-value pairs
     */
    Map<String, String> getAttr(String id, String type);
}
