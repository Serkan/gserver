package org.test.gserver.internal;

import org.test.gserver.GraphNode;
import org.test.gserver.GraphStorage;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * {@link org.test.gserver.GraphNode} implementation; provides lazy
 * loading of node attributes and neighbor info through storage
 * abstraction.
 *
 * @author serkan
 */
class GraphNodeProxyImpl extends GraphNode {

    private final GraphStorage storage;

    protected GraphNodeProxyImpl(String id, String type, GraphStorage storage) {
        super(id, type);
        this.storage = storage;
        storage.createNode(id, type);
    }

    @Override
    public void addNeighbor(GraphNode target) {
        storage.addNeighbor(getId(), getType(), target);
    }

    @Override
    public List<GraphNode> getNeighbors() {
        List<GraphNode> neighbors = storage.getNeighbors(getId(), getType());
        if (neighbors != null && neighbors.size() > 0) {
            return Collections.unmodifiableList(neighbors);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void putAttr(Map<String, String> attr) {
        storage.putAttr(getId(), getType(), attr);
    }

    @Override
    public Map<String, String> gettAttr() {
        return storage.getAttr(getId(), getType());
    }


    @Override
    public int hashCode() {
        return (getId() + getType()).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return o.hashCode() == this.hashCode();
    }
}
