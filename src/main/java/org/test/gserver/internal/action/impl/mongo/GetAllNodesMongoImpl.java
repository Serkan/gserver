package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.GraphNode;
import org.test.gserver.internal.action.GetAllNodesAction;

import java.util.List;

/**
 * Created by serkan on 30.08.2015.
 */
public class GetAllNodesMongoImpl implements GetAllNodesAction {


    private String graphId;

    @Override
    public void configure(Object... params) {
        graphId = (String) params[0];
    }

    @Override
    public List<GraphNode> execute() {
        if (graphId == null) {
            throw new NullPointerException("GraphId must be given via " +
                    "configure method before the execution");
        }
        return null;
    }

    @Override
    public void undo() {

    }
}
