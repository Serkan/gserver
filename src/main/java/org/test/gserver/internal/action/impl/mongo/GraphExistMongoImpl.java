package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.internal.action.GraphExistAction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sturgut on 9/2/15.
 */
public class GraphExistMongoImpl extends AbstractMongoAction implements GraphExistAction {

    @Override
    public void configure(Object... params) {
        loadGraphIdFromParams(params);
    }

    @Override
    public Boolean execute() {
        Map<String, Object> document = new HashMap<>();
        document.put("graphId", getGraphId());
        document.put("documentType", "header");
        return documentDAO.count(document) != 0;
    }

    @Override
    public void undo() {

    }
}
