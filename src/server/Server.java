package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {



    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/info", new InfoHandler());
        server.createContext("/get", new GetHandler());
        server.createContext("/post", new PostHandler());

        server.setExecutor(null); // creates a default executor
        server.start();
    }

    // http://localhost:8000/info
    static class InfoHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = "Use /get?hello=word&foo=bar to see how to handle url parameters";
            Server.writeResponse(httpExchange, response);
        }
    }

    static class GetHandler implements HttpHandler {

        @SuppressWarnings("unchecked")
        public void handle(HttpExchange httpExchange) throws IOException {
            StringBuilder response = new StringBuilder();
            Map <String,String> parms = Server.queryToMap(httpExchange.getRequestURI().getQuery());

            JSONObject obj = new JSONObject();
            obj.put("hello", parms.get("hello"));
            obj.put("foo", parms.get("foo"));
            StringWriter out = new StringWriter();
            obj.writeJSONString(out);
            String jsonText = out.toString();

            response.append(jsonText);
            Server.writeResponse(httpExchange, response.toString());
        }
    }

    static class PostHandler implements HttpHandler {

        @SuppressWarnings("unchecked")
        public void handle(HttpExchange httpExchange) throws IOException {
            StringBuilder response = new StringBuilder();

            String qry;
            try (InputStream in = httpExchange.getRequestBody()) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte buf[] = new byte[4096];
                for (int n = in.read(buf); n > 0; n = in.read(buf)) {
                    out.write(buf, 0, n);
                }
                qry = new String(out.toByteArray());
            }

            System.out.println(qry);

            Map <String,String>parms = Server.queryToMap(qry);

            JSONObject obj = new JSONObject();
            obj.put("hello", parms.get("hello"));
            obj.put("foo", parms.get("foo"));
            StringWriter out = new StringWriter();
            obj.writeJSONString(out);
            String jsonText = out.toString();

            response.append(jsonText);
            Server.writeResponse(httpExchange, response.toString());
        }
    }

    private static void writeResponse(HttpExchange httpExchange, String response) throws IOException {
        Headers responseHeaders = httpExchange.getResponseHeaders();
        responseHeaders.set("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }


    /**
     * returns the url parameters in a map
     * @param query
     * @return map
     */
    private static Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }

}
