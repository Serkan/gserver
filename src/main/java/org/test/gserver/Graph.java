package org.test.gserver;

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
     * @param id   id of node
     * @param type type of node
     * @return node representation which resides in the working graph
     */
    GraphNode createNode(String id, String type);

    /**
     * Removes a node from graph and all incoming edges to given node.
     *
     * @param id   id of node
     * @param type type of node
     */
    void removeNode(String id, String type);

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

}
