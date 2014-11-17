package org.test.gserver.algorithm;

import org.test.gserver.GraphEdge;
import org.test.gserver.GraphNode;

import java.util.*;

/**
 * Shortest path algorithm implementation for unweighted graph.
 */
public final class DijkstraShortestPath {

    public static Queue<GraphNode> path(GraphNode start, GraphNode end) {
        Map<GraphNode, GraphNode> prev = new HashMap<>();
        Set<GraphNode> queued = new HashSet<>();
        Queue<GraphNode> q = new LinkedList<>();
        boolean found = false;

        queued.add(start);
        q.add(start);
        GraphNode current = null;
        while (!q.isEmpty()) {
            current = q.poll();
            if (current.equals(end)) {
                found = true;
                break;
            } else {
                List<GraphEdge> neighbors = current.getNeighbors();
                for (GraphEdge n : neighbors) {
                    GraphNode neighbor = n.getTarget();
                    if (queued.contains(neighbor)) {
                        continue;
                    }
                    prev.put(neighbor, current);
                    queued.add(neighbor);
                    q.add(neighbor);
                }
            }
        }
        if (!found) {
            return null;
        }
        Queue<GraphNode> path = new LinkedList<>();
        // backtracking
        for (GraphNode c = current; c != null; c = prev.get(c)) {
            path.add(c);
        }
        return path;

    }

}
