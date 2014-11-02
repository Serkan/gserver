package org.test.gserver.data;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.test.gserver.Graph;
import org.test.gserver.internal.GFactory;
import org.test.gserver.visualization.cyto.CytoScapeVisualizer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * Simple http server to response http visualization requests.
 * Its written for test purposes.
 *
 * @author serkan
 */
public class GraphServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Graph serkanFacebook = GFactory.get("SerkanFB");

        CytoScapeVisualizer visualizer = new CytoScapeVisualizer();
        serkanFacebook.traverse(visualizer);
        final String renderedResult = visualizer.getRenderedResult();
        System.out.println(renderedResult);

        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 7070), 0);
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                httpExchange.sendResponseHeaders(200, renderedResult.getBytes().length);
                OutputStream responseBody = httpExchange.getResponseBody();
                responseBody.write(renderedResult.getBytes());
                responseBody.close();
            }
        });
        server.start();
    }

}
