package org.test.gserver.visualization.cyto;

import com.google.gson.Gson;
import org.test.gserver.GraphNode;
import org.test.gserver.visualization.GraphWebVisualizer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by serkan on 01.11.2014.
 */
public class CytoScapeVisualizer implements GraphWebVisualizer {

    private HashSet<GraphNode> visited = new HashSet<>();

    private List<Node> nodes = new LinkedList<>();

    private List<Edge> edges = new LinkedList<>();

    @Override
    public void visit(GraphNode node) {
        if (visited.contains(node)) {
            return;
        }
        visited.add(node);
        String name = getName(node);
        String icon = getIcon(node);
        Node n = new Node(getCompositeId(node), name, icon);
        nodes.add(n);
        List<GraphNode> neighbors = node.getNeighbors();
        for (GraphNode neighbor : neighbors) {
            edges.add(new Edge(getCompositeId(node), getCompositeId(neighbor)));
        }
    }

    private String getIcon(GraphNode node) {
        //TODO (serkan)
        return null;
    }

    private String getName(GraphNode node) {
        //TODO (serkan)
        return null;
    }

    @Override
    public String getRenderedResult() {
        Gson gson = new Gson();
        return gson.toJson(new ElementContainer(nodes, edges));
    }


    private String getCompositeId(GraphNode node) {
        return node.getType() + "[" + node.getId() + "]";
    }


    private class ElementContainer {

        private List<Node> nodes;

        private List<Edge> edges;

        ElementContainer(List<Node> nodes, List<Edge> edges) {
            this.nodes = nodes;
            this.edges = edges;
        }

        public List<Node> getNodes() {
            return nodes;
        }

        public void setNodes(List<Node> nodes) {
            this.nodes = nodes;
        }

        public List<Edge> getEdges() {
            return edges;
        }

        public void setEdges(List<Edge> edges) {
            this.edges = edges;
        }
    }

}
