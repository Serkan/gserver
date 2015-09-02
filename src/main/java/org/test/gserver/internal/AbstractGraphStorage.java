package org.test.gserver.internal;

import org.test.gserver.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.test.gserver.internal.ActionType.*;

/**
 * Created by serkan on 30.08.2015.
 */
public abstract class AbstractGraphStorage implements GraphStorage {

    private final String graphId;
    private final GraphActionFactory actionFactory;

    public AbstractGraphStorage(String graphId, GraphActionFactory actionFactory) {
        // you must set graph id before everything because storage highly dependent on graphId
        this.graphId = graphId;
        this.actionFactory = actionFactory;
        // ensure graph is exist in storage
        Boolean graphExist = delegate(GRAPH_EXIST);
        if (!graphExist) {
            delegate(CREATE_GRAPH);
        }
        // ensure indexes
        Map<String, Object> document = new HashMap<>();
        document.put("graphId", 1);
        document.put("key", 1);
        delegate(CREATE_INDEX, document);
    }

    protected <T> T lookup(ActionType type) {
        GraphAction action = actionFactory.lookup(type);
        if (action == null) {
            throw new IllegalArgumentException("Provided action " +
                    "factory does not support specified type : " + type);
        }
        return (T) action;
    }

    protected abstract <T> T delegate(ActionType actionType, Object... params);

    protected String getGraphId() {
        return graphId;
    }

    @Override
    public boolean nodeExist(NodeKey key) {
        return delegate(NODE_EXIST, key);
    }

    @Override
    public List<GraphNode> nodes() {
        return delegate(GET_ALL_NODES, this);
    }

    @Override
    public List<GraphEdge> edges() {
        return delegate(GET_ALL_EDGES, this);
    }

    @Override
    public List<GraphNode> roots() {
        throw new UnsupportedOperationException("NOT IMPLEMENTED!!!");
    }

    @Override
    public void removeNode(NodeKey key) {
        delegate(REMOVE_NODE, key);
    }

    @Override
    public void createNode(NodeKey key) {
        delegate(CREATE_NODE, key);
    }

    @Override
    public void addNeighbor(NodeKey key, GraphNode target, Map<String, String> attr) {
        delegate(ADD_EDGE, key, target, attr);
    }

    @Override
    public List<GraphEdge> getNeighbors(NodeKey key) {
        return delegate(GET_NEIGHBORS, key, this);
    }

    @Override
    public List<GraphEdge> getEdges(NodeKey source, NodeKey target) {
        return delegate(GET_EDGES, source, target, this);
    }

    @Override
    public void putAttr(NodeKey key, Map<String, String> attr) {
        delegate(PUT_NODE_ATTR, key, attr);
    }

    @Override
    public Map<String, String> getAttr(NodeKey key) {
        return delegate(GET_NODE_ATTR, key);
    }

    @Override
    public int nodesSize() {
        return delegate(NODE_SIZE);
    }

}
