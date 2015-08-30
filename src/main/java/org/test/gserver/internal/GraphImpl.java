package org.test.gserver.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.test.gserver.Graph;
import org.test.gserver.GraphEdge;
import org.test.gserver.GraphException;
import org.test.gserver.GraphNode;
import org.test.gserver.GraphStorage;
import org.test.gserver.NodeKey;
import org.test.gserver.Visitor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.test.gserver.GraphException.*;

/**
 * Simple graph implantation, only supports directed graphs.
 *
 * @author serkan
 */
class GraphImpl implements Graph {

    private String id;

    private GraphStorage storage;

    private Logger logger = LoggerFactory.getLogger(GraphImpl.class);

    public GraphImpl(String id, GraphStorage storage) {
        this.id = id;
        this.storage = storage;
    }

    @Override
    public GraphNode createOrGetNode(NodeKey key) {
        return new GraphNodeProxyImpl(key, storage);
    }

    @Override
    public List<GraphEdge> getEdge(NodeKey source, NodeKey target) {
        return storage.getEdges(source, target);
    }

    @Override
    public void removeNode(NodeKey key) throws GraphException {
        storage.removeNode(key);
    }

    @Override
    public void traverse(Visitor visitor) {
        List<GraphNode> nodes = storage.nodes();
        for (GraphNode node : nodes) {
            visitor.visit(node);
        }
    }

    @Override
    public void bfs(Visitor visitor) {
        Queue<GraphNode> queue = new LinkedList<>();
        HashSet<GraphNode> visited = new HashSet<>();
        List<GraphNode> roots = storage.roots();
        for (GraphNode root : roots) {
            if (!visited.contains(root)) {
                visited.add(root);
                queue.add(root);
                while (!queue.isEmpty()) {
                    GraphNode next = queue.poll();
                    List<GraphEdge> neighbors = next.getNeighbors();
                    for (GraphEdge neighbor : neighbors) {
                        queue.add(neighbor.getTarget());
                    }
                    visitor.visit(next);
                }
            }
        }
    }

    @Override
    public Queue<GraphNode> detectCycle() {
        Queue<GraphNode> path = new LinkedList<>();
        // depth-first search on graph
        dfs(new Visitor() {

            @Override
            public void visit(GraphNode node) {
                List<GraphEdge> neighbors = node.getNeighbors();
                for (GraphEdge neighbor : neighbors) {
                    GraphNode target = neighbor.getTarget();
                    // TODO
                }
            }
        });
        return path;
    }

    @Override
    public void dfs(Visitor visitor) {
        // TODO depth first search
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void removeAll() {
        throw new UnsupportedOperationException("DELETED METHOD");
    }

    @Override
    public void beginTx() throws GraphException {
        storage.markCheckPoint();
    }

    @Override
    public void commitTx() throws GraphException {
        // nothing to do
    }

    @Override
    public void rollbackTx() throws GraphException {
        // nothing to do
    }

    @Override
    public void undo() throws GraphException {
        storage.undo();
    }

    @Override
    public void redo() throws GraphException {
        storage.redo();
    }

    @Override
    public List<GraphNode> nodes() {
        return storage.nodes();
    }

    @Override
    public List<GraphEdge> edges() {
        return storage.edges();
    }

    @Override
    public int nodeSize() {
        return storage.nodesSize();
    }

}
