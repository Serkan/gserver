package org.test.gserver.data;

import org.test.gserver.*;
import org.test.gserver.internal.GFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TesterRunner {

    public static void main(String[] args) {
//        prepareGraph();

//        neighborsSizeTest();
//
//        traverseTest();

        removeTest();

    }

    private static void removeTest() {
        Graph test = GFactory.get("test");
        NodeKey fikriKey = new NodeKey("kisi");
        fikriKey.put("vkn", "5457489768");
        fikriKey.put("tckn", "65455643");
        test.removeNode(fikriKey);
    }

    private static void neighborsSizeTest() {
        Graph test = GFactory.get("test");

        NodeKey serkanKey = new NodeKey("kisi");
        serkanKey.put("vkn", "547545455");
        serkanKey.put("tckn", "123456748");
        GraphNode node = test.createOrGetNode(serkanKey);
        List<GraphEdge> neighbors = node.getNeighbors();
        assert neighbors.size() != 1;
    }

    private static void traverseTest() {
        Graph test = GFactory.get("test");

        NodeKey serkanKey = new NodeKey("kisi");
        serkanKey.put("vkn", "547545455");
        serkanKey.put("tckn", "123456748");

        GraphNode serkan = test.createOrGetNode(serkanKey);

        test.traverse(new Visitor() {
            @Override
            public void visit(GraphNode node) {
                NodeKey key = node.getKey();
                System.out.println("KEYS");
                Map<String, String> attr = node.gettAttr();
                for (Map.Entry<String, String> entry : attr.entrySet()) {
                    System.out.println(entry.getKey() + "  " + entry.getValue());
                }
            }
        });
    }

    private static void prepareGraph() {
        Graph test = GFactory.get("test");

        NodeKey serkanKey = new NodeKey("kisi");
        serkanKey.put("vkn", "547545455");
        serkanKey.put("tckn", "123456748");

        GraphNode serkan = test.createOrGetNode(serkanKey);
        Map<String, String> attr = new HashMap<>();
        attr.put("ad", "Serkan");
        attr.put("soyad", "Turgut");
        serkan.putAttr(attr);

        NodeKey fikriKey = new NodeKey("kisi");
        fikriKey.put("vkn", "5457489768");
        fikriKey.put("tckn", "65455643");
        GraphNode fikri = test.createOrGetNode(fikriKey);
        attr = new HashMap<>();
        attr.put("ad", "Fikri");
        attr.put("soyad", "Turgut");
        fikri.putAttr(attr);

        Map<String, String> edgeAttr = new HashMap<>();
        edgeAttr.put("Yakinlik", "Babasi");

        serkan.addNeighbor(fikri, edgeAttr);
    }


}
