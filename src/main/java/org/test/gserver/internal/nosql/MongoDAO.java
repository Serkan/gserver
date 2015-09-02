package org.test.gserver.internal.nosql;

import com.mongodb.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author serkan
 */
public class MongoDAO implements DocumentDAO {

    private static final String HOST;
    private static final int PORT;

    // load configuration
    static {
        InputStream ps = MongoDAO.class.getResourceAsStream("/g.properties");
        Properties props = new Properties();
        try {
            props.load(ps);
        } catch (IOException e) {
            throw new RuntimeException("Mongo configuration is not initialized");
        }
        HOST = props.get("MONGO_IP").toString().trim();
        PORT = Integer.parseInt(props.get("MONGO_PORT").toString().trim());
    }

    private final DBCollection graph;
    private final DBCollection locks;
    private final DBCollection keys;
    private final DBCollection dump;
    private MongoClient mongoClient;

    public MongoDAO() {
        try {
            mongoClient = new MongoClient(HOST, PORT);
            graph = mongoClient.getDB("graph").getCollection("visia");
            dump = mongoClient.getDB("graph").getCollection("dump");
            locks = mongoClient.getDB("graph").getCollection("lockcollection");
            keys = mongoClient.getDB("graph").getCollection("keycollection");

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        // ensure lock indexing is unique and defined !IT IS IMPORTANT FOR LOCKING MECHANISM!!!!!!!!
        BasicDBObject lockIdx = new BasicDBObject();
        lockIdx.put("key", 1);
        lockIdx.put("graphId", 1);
        BasicDBObject lockIdxOptions = new BasicDBObject();
        lockIdxOptions.put("unique", true);
        locks.ensureIndex(lockIdx, lockIdxOptions);
    }

    @Override
    public void save(Map<String, Object> document) {
        BasicDBObject doc = new BasicDBObject(document);
        graph.save(doc);
        // add id back to given document
        document.put("_id", doc.get("_id"));
    }

    @Override
    public void update(Map<String, Object> d1, Map<String, Object> d2) {
        BasicDBObject o = new BasicDBObject(d1);
        BasicDBObject n = new BasicDBObject(d2);
        graph.update(o, n);
    }

    @Override
    public void delete(Map<String, Object> document) {
        BasicDBObject doc = new BasicDBObject(document);
        graph.remove(doc);
    }

    @Override
    public long count(Map<String, Object> example) {
        BasicDBObject doc = new BasicDBObject(example);
        return graph.count(doc);
    }

    @Override
    public Map<String, Object> findOne(Map<String, Object> example) {
        BasicDBObject doc = new BasicDBObject(example);
        DBObject one = graph.findOne(doc);
        return one != null ? one.toMap() : null;
    }

    @Override
    public List<Map<String, Object>> find(Map<String, Object> example) {
        BasicDBObject doc = new BasicDBObject(example);
        DBCursor dbObjects = graph.find(doc);
        List<Map<String, Object>> result = new LinkedList<>();
        while (dbObjects.hasNext()) {
            DBObject next = dbObjects.next();
            result.add(next.toMap());
        }
        return result;
    }

    @Override
    public void moveToDump(Map<String, Object> document) {
        DBCursor cursor = graph.find(new BasicDBObject(document));
        while (cursor.hasNext()) {
            DBObject next = cursor.next();
            graph.remove(next);
            dump.save(next);
        }
    }

    @Override
    public void moveFromDump(Map<String, Object> document) {
        DBCursor cursor = dump.find(new BasicDBObject(document));
        while (cursor.hasNext()) {
            DBObject next = cursor.next();
            dump.remove(next);
            graph.save(next);
        }
    }

    @Override
    public void moveKeyToDump(Map<String, String> document) {
        DBCursor cursor = keys.find(new BasicDBObject(document));
        while (cursor.hasNext()) {
            DBObject next = cursor.next();
            keys.remove(next);
            dump.save(next);
        }
    }

    @Override
    public void moveKeyFromDump(Map<String, String> document) {
        DBCursor cursor = dump.find(new BasicDBObject(document));
        while (cursor.hasNext()) {
            DBObject next = cursor.next();
            dump.remove(next);
            keys.save(next);
        }
    }

    @Override
    public Map<String, Object> findKeyFromDump(Map<String, Object> obj) {
        BasicDBObject doc = new BasicDBObject(obj);
        DBObject one = dump.findOne(doc);
        return one != null ? one.toMap() : null;
    }

    @Override
    public void createIndex(Map<String, Object> document) {
        // TODO not implemented YET
    }

    @Override
    public boolean upsertLock(Map<String, Object> lockObj, Map<String, Object> lockQuery) {
        BasicDBObject l = new BasicDBObject(lockObj);
        BasicDBObject q = new BasicDBObject(lockQuery);

        BasicDBObject expireLessThanOperator = new BasicDBObject();
        expireLessThanOperator.put("$lt", lockQuery.get("expires"));
        q.put("expires", expireLessThanOperator);

        // try with upsert
        boolean result = false;
        try {
            locks.findAndModify(q, null, null, false, l, false, true);
            result = true;
        } catch (MongoException e) {
            if (e.getCode() == 11000) {
                result = false;
            }
        }
        return result;
    }

    @Override
    public void deleteLock(Map<String, Object> document) {
        BasicDBObject doc = new BasicDBObject(document);
        locks.remove(doc);
    }

    @Override
    public Map<String, Object> findKey(Map<String, Object> example) {
        BasicDBObject doc = new BasicDBObject(example);
        DBObject one = keys.findOne(doc);
        return one != null ? one.toMap() : null;
    }

    @Override
    public void deleteKey(Map<String, Object> example) {
        BasicDBObject doc = new BasicDBObject(example);
        keys.remove(doc);
    }

    @Override
    public void saveKey(Map<String, Object> obj) {
        BasicDBObject doc = new BasicDBObject(obj);
        keys.save(doc);
        // add id back to given document
        obj.put("_id", doc.get("_id"));
    }

    @Override
    public long countKeys(Map<String, Object> document) {
        BasicDBObject doc = new BasicDBObject(document);
        return keys.count(doc);
    }

    @Override
    public void updateKey(Map<String, Object> d1, Map<String, Object> d2) {
        BasicDBObject o = new BasicDBObject(d1);
        BasicDBObject n = new BasicDBObject(d2);
        keys.update(o, n);
    }


}
