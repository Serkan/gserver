package org.test.gserver.internal;

import org.test.gserver.Graph;
import org.test.gserver.GraphNode;
import org.test.gserver.GraphStorage;
import org.test.gserver.Visitor;

import java.util.HashSet;
import java.util.List;

/**
 * Simple graph implantation, only supports for directed graphs.
 *
 * @author serkan
 */
class GraphImpl implements Graph {

    private String id;

    private GraphStorage storage;

    public GraphImpl(String id, GraphStorage storage) {
        this.id = id;
        this.storage = storage;
    }

    @Override
    public void removeNode(String id, String type) {
        storage.removeNode(id, type);
    }

    @Override
    public void traverse(Visitor visitor) {
        List<GraphNode> nodes = storage.nodes();
        HashSet<String> visited = new HashSet<>();
        for (GraphNode node : nodes) {
            visitor.visit(node);
        }
    }

    @Override
    public void bfs(Visitor visitor) {
        // TODO breadth first search
    }

    @Override
    public void dfs(Visitor visitor) {
        // TODO depth first search
    }

    @Override
    public GraphNode createNode(String id, String type) {
        return new GraphNodeProxyImpl(id, type, storage);
    }
}
