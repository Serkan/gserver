package org.test.gserver.internal;

/**
 * Created by serkan on 29.08.2015.
 */
public enum ActionType {

    GET_ALL_NODES,
    GET_ALL_EDGES,
    GET_ROOTS,
    CREATE_NODE,
    GET_NODE,
    REMOVE_NODE,
    ADD_EDGE,
    REMOVE_EDGE,
    PUT_NODE_ATTR,
    GET_NODE_ATTR,
    GET_NEIGHBORS,
    GET_EDGES,
    NODE_SIZE,
    GRAPH_EXIST,
    CREATE_GRAPH,
    CREATE_INDEX,
    ACQUIRE_LOCK,
    RELEASE_LOCK,
    NODE_EXIST
}
