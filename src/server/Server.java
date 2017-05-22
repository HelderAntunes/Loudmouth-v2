package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.net.httpserver.*;

public class Server {

    private ConcurrentHashMap<String, String> user_passord = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {
        new Server();
    }

    public Server() throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8000), 0);
        Handler handler = new Handler(this);

        httpServer.createContext("/login", handler);
        httpServer.createContext("/register", handler);
        httpServer.createContext("/info", handler);
        /*
        HttpContext hc1 = server.createContext("/post", handler);
        hc1.setAuthenticator(new BasicAuthenticator("post") {
            @Override
            public boolean checkCredentials(String user, String pwd) {
                return user.equals("admin") && pwd.equals("password");
            }
        });*/

        httpServer.setExecutor(null);
        httpServer.start();
    }

    public boolean insertUser(String username, String password) {
        if (user_passord.containsKey(username))
            return false;
        user_passord.put(username, password);
        return true;
    }

    public boolean userExist(String username, String password) {
        String password_ = user_passord.get(username);
        if (password.equals(password_))
            return true;
        return false;
    }

    public boolean userExist(String username) {
        if (user_passord.containsKey(username))
            return true;
        return false;
    }
}
