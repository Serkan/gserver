package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.internal.action.CreateGraphAction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sturgut on 9/2/15.
 */
public class CreateGraphMongoImpl extends AbstractMongoAction implements CreateGraphAction {

    @Override
    public void configure(Object... params) {
        loadGraphIdFromParams(params);
    }

    @Override
    public Void execute() {
        Map<String, Object> document = new HashMap<>();
        document.put("graphId", getGraphId());
        document.put("documentType", "header");
        document.put("currentVersion", 0);
        document.put("lastVersion", 0);
        documentDAO.save(document);
        return null;
    }

    @Override
    public void undo() {
        // nothing to do
    }
}
