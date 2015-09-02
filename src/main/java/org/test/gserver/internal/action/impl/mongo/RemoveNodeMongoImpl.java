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
        documentDAO.moveToDump(document);

        // outgoing edges
        Map<String, Object> edge = new HashMap<>();
        edge.put("documentType", "edge");
        edge.put("graphId", getGraphId());
        edge.put("source", createOrGetKeyDocumentAndGetId(nodeKey));
        documentDAO.moveToDump(edge);
        // incoming edges
        edge.clear();
        edge.put("documentType", "edge");
        edge.put("graphId", getGraphId());
        edge.put("target", createOrGetKeyDocumentAndGetId(nodeKey));
        documentDAO.moveToDump(edge);

        documentDAO.moveKeyToDump(nodeKey);

        return null;
    }

    @Override
    public void undo() {

        Map<String, Object> document = new HashMap<>();
        document.put("graphId", getGraphId());
        document.put("documentType", "node");
        Object oID = createOrGetKeyDocumentAndGetIdFromDump(nodeKey);
        document.put("key", oID);
        documentDAO.moveFromDump(document);

        documentDAO.moveKeyFromDump(nodeKey);

        // outgoing edges
        Map<String, Object> edge = new HashMap<>();
        edge.put("documentType", "edge");
        edge.put("graphId", getGraphId());
        edge.put("source", createOrGetKeyDocumentAndGetId(nodeKey));
        documentDAO.moveFromDump(edge);
        // incoming edges
        edge.clear();
        edge.put("documentType", "edge");
        edge.put("graphId", getGraphId());
        edge.put("target", createOrGetKeyDocumentAndGetId(nodeKey));
        documentDAO.moveFromDump(edge);

    }

}
