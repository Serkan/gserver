package org.test.gserver.internal;

import org.test.gserver.GraphAction;

/**
 * Created by serkan on 29.08.2015.
 */
public interface GraphActionFactory {

    GraphAction lookup(ActionType action);

}
