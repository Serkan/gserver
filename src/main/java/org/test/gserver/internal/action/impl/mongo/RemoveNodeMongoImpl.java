package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.NodeKey;
import org.test.gserver.internal.action.RemoveNodeAction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by serkan on 30.08.2015.
 */
public class RemoveNodeMongoImpl extends AbstractMongoAction implements RemoveNodeAction {

    private NodeKey nodeKey;

    @Override
    public void configure(Object... params) {
        loadGraphIdFromParams(params);
        nodeKey = (NodeKey) params[1];
    }

    @Override
    public Void execute() {
        if (nodeKey == null) {
            throw new NullPointerException("NodeKey must be given with configure before execution");
        }
        Map<String, Object> document = new HashMap<>();
        document.put("graphId", getGraphId());
        document.put("documentType", "node");
        Object oID = createOrGetKeyDocumentAndGetId(nodeKey);
        document.put("key", oID);
        documentDAO.delete(document);

        removeOutgoingEdges(nodeKey);
        removeIncomingEdges(nodeKey);

        // remove key container document
        deleteNodeKey(oID);
        return null;
    }

    @Override
    public void undo() {

    }
}
