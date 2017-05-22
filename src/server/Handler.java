package server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Handler implements HttpHandler {

    private Server server;

    Handler(Server server) {
        this.server = server;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();

        switch (path) {
            case "/login":
                handleLogin(httpExchange);
                break;
            case "/register":
                handleRegister(httpExchange);
                break;
            default:
                handleInfo(httpExchange);
                break;
        }
    }

    private void handleLogin(HttpExchange httpExchange) throws IOException {
        String query = getQueryOfPostRequest(httpExchange);
        Map<String,String> params = queryToMap(query);
        String username = params.get("username");
        String password = params.get("password");

        JSONObject obj = new JSONObject();
        if (server.userExist(username, password)) {
            obj.put("msg", "success: Login successfully.");
        }
        else {
            obj.put("msg", "error: Username or password are wrong.");
        }
        String jsonText = jsonToString(obj);

        writeResponse(httpExchange, jsonText);
    }

    private void handleRegister(HttpExchange httpExchange) throws IOException {
        String query = getQueryOfPostRequest(httpExchange);
        Map<String,String> params = queryToMap(query);
        String username = params.get("username");
        String password = params.get("password");
        System.out.println(username + " " + password);

        JSONObject obj = new JSONObject();
        if (server.userExist(username)) {
            obj.put("msg", "error: username " + username + " already exists.");
        }
        else {
            server.insertUser(username, password);
            obj.put("msg", "success: user registered successfully.");
        }
        String jsonText = jsonToString(obj);
        writeResponse(httpExchange, jsonText);
    }

    private void handleInfo(HttpExchange httpExchange) throws IOException {
        String response = "Use /get?hello=word&foo=bar to see how to handle url parameters";
        writeResponse(httpExchange, response);
    }

    private String jsonToString(JSONObject jsonObject) throws IOException {
        StringWriter out = new StringWriter();
        jsonObject.writeJSONString(out);
        return out.toString();
    }

    private void handleGet(HttpExchange httpExchange) throws IOException {
        StringBuilder response = new StringBuilder();
        Map<String,String> parms = queryToMap(httpExchange.getRequestURI().getQuery());

        JSONObject obj = new JSONObject();
        obj.put("hello", parms.get("hello"));
        obj.put("foo", parms.get("foo"));
        StringWriter out = new StringWriter();
        obj.writeJSONString(out);
        String jsonText = out.toString();

        response.append(jsonText);
        writeResponse(httpExchange, response.toString());
    }

    private void handlePost(HttpExchange httpExchange) throws IOException {
        StringBuilder response = new StringBuilder();

        String qry = getQueryOfPostRequest(httpExchange);
        System.out.println(qry);

        Map <String,String>parms = queryToMap(qry);

        JSONObject obj = new JSONObject();
        obj.put("hello", httpExchange.getPrincipal().getUsername());
        obj.put("foo", parms.get("foo"));
        StringWriter out = new StringWriter();
        obj.writeJSONString(out);
        String jsonText = out.toString();

        response.append(jsonText);
        writeResponse(httpExchange, response.toString());
    }

    private String getQueryOfPostRequest(HttpExchange httpExchange) throws IOException {
        InputStream in = httpExchange.getRequestBody();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte buf[] = new byte[4096];

        for (int n = in.read(buf); n > 0; n = in.read(buf))
            out.write(buf, 0, n);
        return new String(out.toByteArray());
    }

    private void writeResponse(HttpExchange httpExchange, String response) throws IOException {
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
        Map<String, String> result = new HashMap<>();
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
