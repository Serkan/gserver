package org.test.gserver.internal;

import org.test.gserver.GraphAction;
import org.test.gserver.NodeKey;
import org.test.gserver.internal.action.CheckPointAction;

import java.util.Stack;

/**
 * Created by serkan on 29.08.2015.
 */
class GraphStorageHistorySupport extends AbstractGraphStorage {

    private final static Stack<GraphAction> undoStack = new Stack<>();
    private final static Stack<GraphAction> redoStack = new Stack<>();

    public GraphStorageHistorySupport(String graphId, GraphActionFactory actionFactory) {
        super(graphId, actionFactory);
    }

    @Override
    protected <T> T delegate(ActionType actionType, Object... params) {
        GraphAction<T> action = lookup(actionType);
        Object[] fullParams = new Object[params.length + 1];
        fullParams[0] = getGraphId();
        System.arraycopy(params, 0, fullParams, 1, params.length);
        action.configure(fullParams);
        // record every step in undo stack and clear redo stack to overwrite
        redoStack.clear();
        undoStack.push(action);
        return action.execute();
    }

    @Override
    public void markCheckPoint() {
        undoStack.push(new CheckPointAction());
    }

    @Override
    public void undo() {
        // pop and undo until see a checkpoint
        // every popped put actions will be pushed to redo stack
        GraphAction action;
        while (!((action = undoStack.pop()) instanceof CheckPointAction)) {
            action.undo();
            redoStack.push(action);
        }
    }

    @Override
    public void redo() {
        while (!redoStack.empty()) {
            GraphAction action = redoStack.pop();
            action.execute();
        }
    }

    @Override
    public boolean atomicLock(String owner, NodeKey key, long maxLockTime) {
        throw new UnsupportedOperationException("History mode does not support locking");
    }

    @Override
    public void releaseLock(String owner, NodeKey key) {
        throw new UnsupportedOperationException("History mode does not support locking");
    }

}
