package org.test.gserver.visualization;

import org.test.gserver.Visitor;

/**
 * Html based visualizers must implement this interface. After visited
 * all nodes they must produce an appropriate json representation
 * depends on javascript visualization library.
 *
 * @author serkan
 */
public interface GraphWebVisualizer extends Visitor {

    /**
     * @return json string formatted as visualization library can interpreted
     */
    String getRenderedResult();

}
