package org.test.gserver.internal.action.impl.mongo;

import org.test.gserver.NodeKey;
import org.test.gserver.internal.action.PutAttrAction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by serkan on 3class0.08.2015.
 */
public class PutAttrMongoImpl extends AbstractMongoAction implements PutAttrAction {

    private NodeKey nodeKey;
    private Map<String, String> attr;

    @Override
    public void configure(Object... params) {
        loadGraphIdFromParams(params);
        nodeKey = (NodeKey) params[1];
        attr = (Map<String, String>) params[2];
    }

    @Override
    public Void execute() {
        if (nodeKey == null
                || attr == null) {
            throw new NullPointerException("None of the parameters can not be null");
        }
        Map<String, Object> document = new HashMap<>();
        document.put("graphId", getGraphId());
        document.put("documentType", "node");
        Object oID = createOrGetKeyDocumentAndGetId(nodeKey);
        document.put("key", oID);
        Map<String, Object> old = documentDAO.findOne(document);
        Map<String, Object> next = copyDocument(old);
        // TODO (serkan) attr must be append not overwrite
        next.put("attr", attr);
        documentDAO.update(old, next);
        return null;
    }

    @Override
    public void undo() {
        Map<String, Object> document = new HashMap<>();
        document.put("graphId", getGraphId());
        document.put("documentType", "node");
        Object oID = createOrGetKeyDocumentAndGetId(nodeKey);
        document.put("key", oID);
        Map<String, Object> old = documentDAO.findOne(document);
        Map<String, Object> next = copyDocument(old);
        // TODO (serkan) attr must be append not overwrite
        next.put("attr", new HashMap<>()); // overwrite blank attribute list
        documentDAO.update(old, next);
    }
}
