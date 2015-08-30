package org.test.gserver.internal.action.impl.mongo;

import com.mongodb.BasicDBObject;
import org.test.gserver.NodeKey;
import org.test.gserver.internal.action.GetAttrAction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by serkan on 30.08.2015.
 */
public class GetAttrMongoImpl extends AbstractMongoAction implements GetAttrAction {

    private NodeKey nodeKey;

    @Override
    public void configure(Object... params) {
        loadGraphIdFromParams(params);
        nodeKey = (NodeKey) params[1];
    }

    @Override
    public Map<String, String> execute() {
        if (nodeKey == null) {
            throw new NullPointerException("NodeKey can not be null");
        }
        Map<String, Object> document = new HashMap<>();
        document.put("graphId", getGraphId());
        document.put("documentType", "node");
        document.put("isActive", true);
        Object oID = createOrGetKeyDocumentAndGetId(nodeKey);

        document.put("key", oID);
        Map<String, Object> node = documentDAO.findOne(document);

        // TODO mongo bagimliligi olustu DAO.getAttr(Obj) gibi bit metod ile cozulebilir
        BasicDBObject attr = (BasicDBObject) node.get("attr");
        Map result;
        if (attr != null) {
            result = attr.toMap();
        } else {
            result = new HashMap<>(1);
        }
        return result;
    }

    @Override
    public void undo() {
        // do nothing
    }
}
