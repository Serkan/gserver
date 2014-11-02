package org.test.gserver.internal;

import org.test.gserver.Graph;

/**
 * Graph factory. Abstraction between graph interface and different
 * graph implementation.
 *
 * @author serkan
 */
public final class GFactory {

    /**
     * Hidden constructor.
     */
    private GFactory() {
    }

    /**
     * Create or get graph for given id.
     *
     * @param id id of graph
     * @return Concrete graph impl
     */
    public static Graph get(String id) {
        return new GraphImpl(id, new MongoStorage(id));
    }

}
