package org.test.gserver;

import java.util.List;
import java.util.Map;

/**
 * Base data structure to keep node id and type.
 *
 * @author serkan
 */
public abstract class GraphNode {

    private String id;

    private String type;

    protected GraphNode(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public abstract void addNeighbor(GraphNode target);

    public abstract List<GraphNode> getNeighbors();

    public abstract void putAttr(Map<String, String> attr);

    public abstract Map<String, String> gettAttr();

    @Override
    public int hashCode() {
        return (id + type).hashCode();
    }
}
