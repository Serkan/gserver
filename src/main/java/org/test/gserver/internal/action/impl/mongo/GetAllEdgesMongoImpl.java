package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.GraphEdge;
import org.test.gserver.internal.action.GetAllEdgesAction;

import java.util.List;

/**
 * Created by serkan on 30.08.2015.
 */
public class GetAllEdgesMongoImpl implements GetAllEdgesAction {

    private String graphId;

    @Override
    public void configure(Object... params) {
        graphId = (String) params[0];
    }

    @Override
    public List<GraphEdge> execute() {
        if (graphId == null) {
            throw new NullPointerException("GraphId can not be null before execution.");
        }
        return null;
    }

    @Override
    public void undo() {

    }
}
