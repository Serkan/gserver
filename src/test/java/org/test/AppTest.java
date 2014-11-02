package org.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.test.gserver.*;
import org.test.gserver.internal.GFactory;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        Graph graph = GFactory.get("testGraph");

        GraphNode kisi = graph.createNode("12911171700", "Kisi");

        GraphNode akraba1 = graph.createNode("111111111111111", "Kisi");
        GraphNode akraba2 = graph.createNode("222222222222222", "Kisi");
        GraphNode akraba3 = graph.createNode("333333333333333", "Kisi");

        kisi.addNeighbor(akraba1);
        kisi.addNeighbor(akraba2);
        kisi.addNeighbor(akraba3);

        akraba2.addNeighbor(akraba3);

        akraba3.addNeighbor(kisi);


        graph.traverse(new Visitor() {

            @Override
            public void visit(GraphNode node) {
                System.out.println(node.getId());
            }
        });


    }
}
