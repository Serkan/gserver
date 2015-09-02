package org.test.gserver.internal.nosql;

import java.util.List;
import java.util.Map;

/**
 * @author serkan
 */
public interface DocumentDAO {

    void save(Map<String, Object> document);

    void update(Map<String, Object> d1, Map<String, Object> d2);

    void delete(Map<String, Object> document);

    long count(Map<String, Object> example);

    Map<String, Object> findOne(Map<String, Object> example);

    List<Map<String, Object>> find(Map<String, Object> example);

    void createIndex(Map<String, Object> document);

    boolean upsertLock(Map<String, Object> lockObj, Map<String, Object> lockQuery);

    void deleteLock(Map<String, Object> document);

    Map<String, Object> findKey(Map<String, Object> example);

    void deleteKey(Map<String, Object> example);

    void saveKey(Map<String, Object> obj);

    long countKeys(Map<String, Object> document);

    void updateKey(Map<String, Object> foundKey, Map<String, Object> keyCopy);

    void moveToDump(Map<String, Object> document);

    void moveFromDump(Map<String, Object> document);

    void moveKeyToDump(Map<String, String> nodeKey);

    void moveKeyFromDump(Map<String, String> document);
}
