package org.test.gserver.internal.action;

import org.test.gserver.GraphAction;

/**
 * Created by serkan on 30.08.2015.
 */
public class CheckPointAction implements GraphAction<Void> {

    @Override
    public void configure(Object... params) {
        throw new IllegalStateException("CheckPoint action " +
                "exist only mark the stack, methods must " +
                "not be called by anywhere");
    }

    @Override
    public Void execute() {
        throw new IllegalStateException("CheckPoint action " +
                "exist only mark the stack, methods must " +
                "not be called by anywhere");
    }

    @Override
    public void undo() {
        throw new IllegalStateException("CheckPoint action " +
                "exist only mark the stack, methods must " +
                "not be called by anywhere");
    }
}
