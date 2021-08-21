package com.ventus.parser.modules;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.ventus.parser.ParserApplication;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ventus.parser.modules.Sender.output_servers;

public class Http implements Runnable {
    private static ExecutorService executorservice = Executors.newSingleThreadExecutor();
    private String name = "";

    public Http(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        executorservice.execute(() -> {
            try {
                HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
                server.createContext("/test", new MyHandler());
                server.createContext("/add", new AddHandler());
                server.createContext("/output", new OutPutHandler());
                server.setExecutor(null); // creates a default executor
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private Map<String, String> handleGetRequests(HttpExchange httpExchange) {
        Map<String, String> map = new HashMap<>();
        for (String s : httpExchange.
                getRequestURI()
                .toString()
                .split("\\?")[1]
                .split("&")) {
            String[] ss = s.split("=");
            map.put(ss[0], URLDecoder.decode(ss[1], StandardCharsets.UTF_8));
        }
        return map;
    }

    private String handleGetRequest(HttpExchange httpExchange) {
        return httpExchange.
                getRequestURI()
                .toString()
                .split("\\?")[1]
                .split("=")[1];
    }

    private class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = name;
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private class AddHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String requestParamValue = null;
            if ("GET".equals(httpExchange.getRequestMethod())) {
                requestParamValue = handleGetRequest(httpExchange);
            }
            if (requestParamValue != null) {
                ParserApplication.loader.addURL(URLDecoder.decode(requestParamValue, StandardCharsets.UTF_8));
            }
            OutputStream outputStream = httpExchange.getResponseBody();
            String htmlResponse = "OK";
            httpExchange.sendResponseHeaders(200, htmlResponse.length());
            outputStream.write(htmlResponse.getBytes());
            outputStream.flush();
            outputStream.close();
        }
    }

    private class OutPutHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            Map<String, String> requestParamValue = null;
            if ("GET".equals(httpExchange.getRequestMethod())) {
                requestParamValue = handleGetRequests(httpExchange);
            }
            String name = requestParamValue.get("name");
            String ip = requestParamValue.get("ip");
            output_servers.put(name, ip);
            OutputStream outputStream = httpExchange.getResponseBody();
            String htmlResponse = "OK";
            httpExchange.sendResponseHeaders(200, htmlResponse.length());
            outputStream.write(htmlResponse.getBytes());
            outputStream.flush();
            outputStream.close();
        }
    }
}
