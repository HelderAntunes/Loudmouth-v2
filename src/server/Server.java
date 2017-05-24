package server;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.net.httpserver.*;

public class Server {

    private ConcurrentHashMap<String, String> user_password = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ArrayList<String>> user_chats = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ArrayList<String>> user_invitations = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ArrayList<ArrayList<String>>> chat_messages = new ConcurrentHashMap<>(); // chat -> [[author, message, date], ...]

    private File dbDir;
    private File dbFile;

    public static void main(String[] args) throws Exception {
        new Server();
    }

    private Server() throws IOException, ClassNotFoundException {
        initDB();

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8000), 0);
        Handler handler = new Handler(this);

        httpServer.createContext("/login", handler);
        httpServer.createContext("/register", handler);
        httpServer.createContext("/info", handler);

        HttpContext hcCreateChat = httpServer.createContext("/createChat", handler);
        HttpContext hcGetMyChats = httpServer.createContext("/getMyChats", handler);
        HttpContext hcGetInvites = httpServer.createContext("/getInvites", handler);
        HttpContext hcInvitation = httpServer.createContext("/invitation", handler);
        HttpContext hcAcceptInvite = httpServer.createContext("/acceptInvite", handler);
        HttpContext hcDeclineInvite = httpServer.createContext("/declineInvite", handler);

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
        hcGetInvites.setAuthenticator(bscAuth);
        hcInvitation.setAuthenticator(bscAuth);
        hcAcceptInvite.setAuthenticator(bscAuth);
        hcDeclineInvite.setAuthenticator(bscAuth);

        httpServer.setExecutor(null);
        httpServer.start();
    }

    private void initDB() throws ClassNotFoundException, IOException {
        dbDir = new File(Utils.DB_DIR_NAME);
        if (!dbDir.exists()) {
            dbDir.mkdir();
        }

        dbFile = new File(dbDir, Utils.DB_FILE_NAME);
        if (dbFile.exists()) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dbFile));
            user_password = (ConcurrentHashMap<String, String>) ois.readObject();
            user_chats = (ConcurrentHashMap<String, ArrayList<String>>) ois.readObject();
            user_invitations = (ConcurrentHashMap<String, ArrayList<String>>) ois.readObject();
            chat_messages = (ConcurrentHashMap<String, ArrayList<ArrayList<String>>>) ois.readObject();
            ois.close();
        }
    }

    public void recordsDatabaseToFile() throws IOException {
        FileOutputStream fout = new FileOutputStream(dbFile);
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(user_password);
        oos.writeObject(user_chats);
        oos.writeObject(user_invitations);
        oos.writeObject(chat_messages);
        oos.close();
        fout.close();
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

    void insertInvitation(String chatName, String username) {
        insertInHashMap(user_invitations, username, chatName);
    }

    ArrayList<String> getUserInvitations(String username) {
        if (!user_invitations.containsKey(username))
            return new ArrayList<>();
        return user_invitations.get(username);
    }

    void acceptInvitation(String username, String chatName) {
        ArrayList<String> userInvitations = getUserInvitations(username);
        if (userInvitations.contains(chatName)) {
            userInvitations.remove(chatName);
            addUserToChat(username, chatName);
        }
        else {
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

    private boolean insertInHashMap(ConcurrentHashMap<String, ArrayList<String>> hashMap, String key, String value) {
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
