package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.GraphNode;
import org.test.gserver.NodeKey;
import org.test.gserver.internal.action.AddNeighborAction;

import java.util.Map;

/**
 * Created by serkan on 30.08.2015.
 */
public class AddNeighborMongoImpl extends AbstractMongoAction implements AddNeighborAction {

    private NodeKey sourceKey;

    private GraphNode target;

    private Map<String, String> attr;

    @Override
    public void configure(Object... params) {
        loadGraphIdFromParams(params);
        sourceKey = (NodeKey) params[1];
        target = (GraphNode) params[2];
        attr = (Map<String, String>) params[3];
    }

    @Override
    public Void execute() {
        if (sourceKey == null
                || target == null
                || attr == null) {
            throw new NullPointerException("None of the parameters " +
                    "(source NodeKey, target GraphNode, " +
                    "edge attributes) can not be null");
        }
        // check if exist
        if (!existEdge(sourceKey, target.getKey(), attr)) {
            // create edge
            createEdge(sourceKey, target.getKey(), attr);
        }
        return null;
    }

    @Override
    public void undo() {

    }
}
