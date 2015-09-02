package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.internal.action.CreateIndexAction;

/**
 * Created by sturgut on 9/2/15.
 */
public class CreateIndexMongoImpl extends AbstractMongoAction implements CreateIndexAction {

    @Override
    public void configure(Object... params) {
        loadGraphIdFromParams(params);
    }

    @Override
    public Void execute() {
        return null;
    }

    @Override
    public void undo() {

    }
}
