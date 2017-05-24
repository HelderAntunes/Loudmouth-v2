package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.net.httpserver.*;

public class Server {

    private ConcurrentHashMap<String, String> user_password = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ArrayList<String>> user_chats = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ArrayList<String>> user_invitations = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ArrayList<ArrayList<String>>> chat_messages = new ConcurrentHashMap<>(); // chat -> [[author, message, date], ...]

    public static void main(String[] args) throws Exception {
        new Server();
    }

    private Server() throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8000), 0);
        Handler handler = new Handler(this);

        httpServer.createContext("/login", handler);
        httpServer.createContext("/register", handler);
        httpServer.createContext("/info", handler);

        HttpContext hcCreateChat = httpServer.createContext("/createChat", handler);
        HttpContext hcGetMyChats = httpServer.createContext("/getMyChats", handler);

        BasicAuthenticator bscAuth = new BasicAuthenticator("user_password") {
            @Override
            public boolean checkCredentials(String user, String pwd) {
                if (userExist(user) && userExist(user, pwd))
                    return true;
                return false;
            }
        };
        hcCreateChat.setAuthenticator(bscAuth);
        hcGetMyChats.setAuthenticator(bscAuth);

        httpServer.setExecutor(null);
        httpServer.start();
    }

    void insertUser(String username, String password) {
        if (user_password.containsKey(username))
            return;
        user_password.put(username, password);
    }

    boolean userExist(String username, String password) {
        String password_ = user_password.get(username);
        return password.equals(password_);
    }

    boolean userExist(String username) {
        return user_password.containsKey(username);
    }

    boolean insertChat(String name) {
        if (chat_messages.containsKey(name))
            return false;
        chat_messages.put(name, new ArrayList<>());
        return true;
    }

    boolean chatExist(String name) {
        return chat_messages.containsKey(name);
    }

    ArrayList<ArrayList<String>> getChatMessages(String chatName) {
        return chat_messages.get(chatName);
    }

    void addMessageToChat(String chatName, ArrayList<String> message) {
        ArrayList<ArrayList<String>> messages = chat_messages.get(chatName);
        messages.add(message);
    }

    boolean addUserToChat(String username, String chatName) {
        return insertInHashMap(user_chats, username, chatName);
    }

    ArrayList<String> getUserChats(String username) {
        if (!user_chats.containsKey(username))
            return new ArrayList<>();
        return user_chats.get(username);
    }

    boolean leaveChat(String username, String chatName) {
        ArrayList<String> chats = getUserChats(username);
        if (chats.contains(chatName)) {
            chats.remove(chatName);
            return true;
        }
        else {
            return false;
        }
    }

    boolean insertInvitation(String chatName, String username) {
        return insertInHashMap(user_invitations, username, chatName);
    }

    ArrayList<String> getUserInvitations(String username) {
        if (!user_invitations.containsKey(username))
            return new ArrayList<>();
        return user_invitations.get(username);
    }

    boolean acceptInvitation(String username, String chatName) {
        ArrayList<String> userInvitations = getUserInvitations(username);
        if (userInvitations.contains(chatName)) {
            userInvitations.remove(chatName);
            addUserToChat(username, chatName);
            return true;
        }
        else {
            return false;
        }
    }

    boolean declineInvitation(String username, String chatName) {
        ArrayList<String> userInvitations = getUserInvitations(username);
        if (userInvitations.contains(chatName)) {
            userInvitations.remove(chatName);
            return true;
        }
        else {
            return false;
        }
    }

    boolean insertInHashMap(ConcurrentHashMap<String, ArrayList<String>> hashMap, String key, String value) {
        if (hashMap.containsKey(key)) {
            ArrayList<String> currInv = hashMap.get(key);
            if (currInv.contains(value))
                return false;
            currInv.add(value);
            return true;
        }
        else {
            hashMap.put(key, new ArrayList<>());
            ArrayList<String> currChats = hashMap.get(key);
            currChats.add(value);
            return true;
        }
    }
}
