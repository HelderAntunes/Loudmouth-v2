package client.gui;

import client.network.HttpClient;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private JPanel contentPane;
    private Login login;
    private Register register;
    private Chats chats;
    private Chat chat;
    private HttpClient httpClient;

    private String username;
    private String password;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        HttpClient httpClient = new HttpClient();
        EventQueue.invokeLater(() -> {
            try {
                MainWindow frame = new MainWindow(httpClient);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the frame.
     */
    public MainWindow(HttpClient httpClient) {
        this.httpClient = httpClient;
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        contentPane = new JPanel(new CardLayout());
        setContentPane(contentPane);
        initialize();
        setVisible(true);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        login = new Login(this, httpClient);
        login.setVisible(true);

        register = new Register(this, httpClient);
        register.setVisible(true);

        chats = new Chats(this, httpClient);
        chats.setVisible(true);

        chat = new Chat(this, httpClient);
        chat.setVisible(true);

        contentPane.add(login.getPanel(), "login");
        contentPane.add(register.getPanel(), "register");
        contentPane.add(chats.getPanel(), "chats");
        contentPane.add(chat.getPanel(), "chat");

        showLayout("login");
    }

    public void showLayout(String layout){
        if (layout.equals("chats"))
            chats.setMyChats();
        CardLayout cardLayout = (CardLayout) contentPane.getLayout();
        cardLayout.show(contentPane, layout);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Chat getChat() {
        return chat;
    }

}
