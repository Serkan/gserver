package org.test.gserver.data;

import org.test.gserver.Graph;
import org.test.gserver.internal.GFactory;

import java.io.IOException;

/**
 * Simple http server to response http visualization requests.
 * Its written for test purposes.
 *
 * @author serkan
 */
public class GraphServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Graph serkanFacebook = GFactory.get("SerkanFB");

//        CytoScapeVisualizer visualizer = new CytoScapeVisualizer();
//        serkanFacebook.traverse(visualizer);
//        final String renderedResult = visualizer.getRenderedResult();
//        System.out.println(renderedResult);
//
//        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 7070), 0);
//        server.createContext("/", new HttpHandler() {
//            @Override
//            public void handle(HttpExchange httpExchange) throws IOException {
//                httpExchange.sendResponseHeaders(200, renderedResult.getBytes().length);
//                OutputStream responseBody = httpExchange.getResponseBody();
//                responseBody.write(renderedResult.getBytes());
//                responseBody.close();
//            }
//        });
//        server.start();
    }

}
