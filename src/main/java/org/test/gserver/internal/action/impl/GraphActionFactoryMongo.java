package org.test.gserver.internal.action.impl;

import org.test.gserver.GraphAction;
import org.test.gserver.internal.ActionType;
import org.test.gserver.internal.GraphActionFactory;
import org.test.gserver.internal.action.impl.mongo.*;

/**
 * Created by serkan on 30.08.2015.
 */
public class GraphActionFactoryMongo implements GraphActionFactory {

    @Override
    public GraphAction lookup(ActionType action) {
        switch (action) {
            case GET_ALL_NODES:
                return new GetAllNodesMongoImpl();
            case GET_ALL_EDGES:
                return new GetAllEdgesMongoImpl();
            case GET_ROOTS:
                return null;
            case CREATE_OR_GET_NODE:
                return new CreateOrGetNodeMongoImpl();
            case REMOVE_NODE:
                return new RemoveNodeMongoImpl();
            case ADD_EDGE:
                return new AddNeighborMongoImpl();
            case REMOVE_EDGE:
                return new RemoveNodeMongoImpl();
            case PUT_NODE_ATTR:
                return new PutAttrMongoImpl();
            case GET_NODE_ATTR:
                return new GetAttrMongoImpl();
            case GET_EDGES:
                return new GetEdgesMongoImpl();
            default:
                throw new IllegalArgumentException("Wrong action type");
        }
    }
}