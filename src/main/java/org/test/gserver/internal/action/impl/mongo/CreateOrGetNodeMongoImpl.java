package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.GraphNode;
import org.test.gserver.NodeKey;
import org.test.gserver.internal.action.CreateOrGetNodeAction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by serkan on 30.08.2015.
 */
public class CreateOrGetNodeMongoImpl extends AbstractMongoAction implements CreateOrGetNodeAction {

    private NodeKey nodeKey;

    @Override
    public void configure(Object... params) {
        loadGraphIdFromParams(params);
        nodeKey = (NodeKey) params[1];
    }

    @Override
    public GraphNode execute() {
        if (nodeKey == null) {
            throw new NullPointerException("NodeKey must be given with " +
                    "configure method before the execution");
        }
        Map<String, Object> document = new HashMap<>();
        document.put("graphId", getGraphId());
        document.put("documentType", "node");
        document.put("isRoot", true);
        Object oID = createOrGetKeyDocumentAndGetId(nodeKey);
        document.put("isActive", true);
        document.put("key", oID);
        if (documentDAO.count(document) < 1) {
            documentDAO.save(document);
        }
    }

    @Override
    public void undo() {

    }
}
