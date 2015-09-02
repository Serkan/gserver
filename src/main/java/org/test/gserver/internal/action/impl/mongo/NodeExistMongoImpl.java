package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.NodeKey;
import org.test.gserver.internal.action.NodeExistAction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sturgut on 9/2/15.
 */
public class NodeExistMongoImpl extends AbstractMongoAction implements NodeExistAction {

    private NodeKey nodeKey;

    @Override
    public void configure(Object... params) {
        loadGraphIdFromParams(params);
        nodeKey = (NodeKey) params[1];
    }

    @Override
    public Boolean execute() {
        Map<String, Object> document = new HashMap<>();
        document.put("graphId", getGraphId());
        document.put("documentType", "node");
        document.put("isRoot", true);
        Object oidFromDump = createOrGetKeyDocumentAndGetIdFromDump(nodeKey);
        if (oidFromDump != null) {
            return false;
        }
        Object oID = createOrGetKeyDocumentAndGetId(nodeKey);
        document.put("key", oID);
        return documentDAO.count(document) > 0;
    }

    @Override
    public void undo() {
        // nothing to do
    }
}
