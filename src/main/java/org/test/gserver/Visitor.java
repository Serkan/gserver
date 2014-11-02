package org.test.gserver;

/**
 * Node visitor, to walk on graph node by node.
 *
 * @author serkan
 */
public interface Visitor {

    /**
     * Visits evey node on the graph. Caller must guarantee every node
     * visited only once,
     *
     * @param node {@link org.test.gserver.GraphNode}
     */
    void visit(GraphNode node);

}
