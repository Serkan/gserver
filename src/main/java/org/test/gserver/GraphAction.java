package org.test.gserver;

/**
 * Created by serkan on 29.08.2015.
 */
public interface GraphAction<R> {

    void configure(Object... params);

    R execute();

    void undo();

}
