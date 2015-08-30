package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.internal.action.NodeSizeAction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by serkan on 30.08.2015.
 */
public class NodeSizeMongoImpl extends AbstractMongoAction implements NodeSizeAction {

    @Override
    public void configure(Object... params) {
        loadGraphIdFromParams(params);
    }

    @Override
    public Integer execute() {
        Map<String, Object> document = new HashMap<>();
        document.put("graphId", getGraphId());
        document.put("isActive", true);
        return (int) documentDAO.countKeys(document);
    }

    @Override
    public void undo() {
        // do nothing
    }
}
