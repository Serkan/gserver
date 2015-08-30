package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.internal.action.NodeSizeAction;

/**
 * Created by serkan on 30.08.2015.
 */
public class NodeSizeMongoImpl implements NodeSizeAction {

    private String graphId;

    @Override
    public void configure(Object... params) {
        graphId = (String) params[0];
    }

    @Override
    public Integer execute() {
        return null;
    }

    @Override
    public void undo() {

    }
}
