package org.test.gserver.internal;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.test.gserver.GraphEdge;
import org.test.gserver.GraphNode;
import org.test.gserver.GraphStorage;
import org.test.gserver.NodeKey;
import org.test.gserver.Pair;
import org.test.gserver.internal.nosql.DocumentDAO;
import org.test.gserver.internal.nosql.MongoDAO;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class DocumentStorage implements GraphStorage {

    private final String graphId;

    private Logger logger = LoggerFactory.getLogger(DocumentStorage.class);

    private DocumentDAO documentDAO = new MongoDAO();

    public DocumentStorage(String graphId) {
        // you must set graph id before everything because storage highly dependent on graphId
        this.graphId = graphId;
        // ensure graph is exist in storage
        if (!graphExists()) {
            createGraph();
        }
        // ensure indexes
        Map<String, Object> document = new HashMap<>();
        document.put("graphId", 1);
        document.put("key", 1);
        documentDAO.createIndex(document);
    }

    @Override
    public boolean atomicLock(String owner, NodeKey key, long maxLongTimeInNano) {
        Map<String, Object> lockDoc = new HashMap<>();
        lockDoc.put("graphId", graphId);
        lockDoc.put("key", key);
        lockDoc.put("owner", owner);
        // set expire time 'maxLockTime' milliseconds after its created
        lockDoc.put("expires", System.nanoTime() + maxLongTimeInNano);

        Map<String, Object> queryDoc = new HashMap<>();
        queryDoc.put("graphId", graphId);
        queryDoc.put("key", key);
        // set expire time 'maxLockTime' milliseconds after its created
        queryDoc.put("expires", System.nanoTime());

        return documentDAO.upsertLock(lockDoc, queryDoc);
    }

    @Override
    public void releaseLock(String owner, NodeKey key) {
        Map<String, Object> lockDoc = new HashMap<>();
        lockDoc.put("key", key);
        lockDoc.put("graphId", graphId);
        documentDAO.deleteLock(lockDoc);
    }

}