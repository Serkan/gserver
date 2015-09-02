package org.test.gserver;

import java.util.List;
import java.util.Queue;

/**
 * Graph API main interface, all functionality provided by the library
 * is exposed through this interface.
 *
 * @author serkan
 */
public interface Graph {

    /**
     * Creates a node on the graph. Only way to create node is to
     * call this method, "new" keyword can not be used for node
     * construction because {@link org.test.gserver.GraphNode}  has a
     * "protected"  class constructor.
     *
     * @return node representation which resides in the working graph
     */
    GraphNode createOrGetNode(NodeKey nodeKey);

    /**
     * Finds and returns fully constructed graph edges.
     *
     * @param source source node key of the requested edge
     * @param target target node key of the requested edge
     * @return list of found edges
     */
    List<GraphEdge> getEdge(NodeKey source, NodeKey target);

    /**
     * Removes a node from graph and all incoming edges to given node.
     */
    void removeNode(NodeKey nodeKey) throws GraphException;

    /**
     * Iterates all graph nodes. It  guarantees that multiple visiting to
     * same node not going to happen.
     *
     * @param visitor node visitor
     */
    void traverse(Visitor visitor);

    /**
     * Breadth first search on the graph, start from graph roots
     * (nodes which has no incoming edge).
     *
     * @param visitor node visitor
     */
    void bfs(Visitor visitor);

    /**
     * Walk on the graph for cycles and returns the first found cycle path.
     *
     * @return cyclic path of the first found cycle otherwise empty queue
     */
    Queue<GraphNode> detectCycle();

    /**
     * Depth first search on the graph, start from graph roots
     * (nodes which has no incoming edge).
     *
     * @param visitor node visitor
     */
    void dfs(Visitor visitor);

    /**
     * Getter for graph id.
     *
     * @return permanent id of graph
     */
    String getId();

    /**
     * Removes all nodes in the graph.
     */
    void removeAll();

    /**
     * Node count of the graphs.
     *
     * @return node size
     */
    int nodeSize();

    /**
     * Begins a graph transaction, after begin the graph either must be
     * committed or rollbacked otherwise graph will be stuck in inconsistent state.
     *
     * @throws GraphException if there is an ongoing transaction
     */
    void beginTx() throws GraphException;

    /**
     * Commits a current transaction.
     *
     * @throws GraphException if there is no transaction to commit
     */
    @Deprecated
    void commitTx() throws GraphException;

    /**
     * Rollbacks the transaction.
     *
     * @throws GraphException if nothing to rollback
     *
     */
    @Deprecated
    void rollbackTx() throws GraphException;

    /**
     * Rewinds the graph transaction one step back.
     * Only affect the changes made through a transaction.
     *
     * @throws GraphException if graph is first position
     */
    void undo() throws GraphException;

    /**
     * Forwards the graph transaction one step further.
     * Only affect teh changes made trough a transaction.
     *
     * @throws GraphException if graph is in last position
     */
    void redo() throws GraphException;

    /**
     * All nodes in the graph.
     *
     * @return list of nodes
     */
    List<GraphNode> nodes();

    /**
     * All edges in the graph.
     *
     * @return list of edges
     */
    List<GraphEdge> edges();

}
