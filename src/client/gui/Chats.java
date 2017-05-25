package client.gui;

import client.network.HttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

public class Chats {
    private MainWindow parent;
    private HttpClient httpClient;
    private JLabel titleLbl;
    private JLabel myChatsTitleLbl;
    private JLabel myInvitationsLbl;
    private JLabel createChatLbl;
    private JComboBox myChatsComboBox;
    private JComboBox invitationsComboBox;
    private JButton enterButton;
    private JButton leaveButton;
    private JButton acceptButton;
    private JButton declineButton;
    private JButton createButton;
    private JTextField chatNameTxtFld;
    private JPanel panel;
    private JButton logOutButton;
    private JLabel createChatInfoLbl;

    public Chats(MainWindow parent, HttpClient httpClient) {
        this.parent = parent;
        this.httpClient = httpClient;
        titleLbl.setFont (titleLbl.getFont ().deriveFont (32.0f));
        myInvitationsLbl.setFont (titleLbl.getFont ().deriveFont (16.0f));
        myChatsTitleLbl.setFont (titleLbl.getFont ().deriveFont (16.0f));
        createChatLbl.setFont (titleLbl.getFont ().deriveFont (16.0f));
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chatName = chatNameTxtFld.getText();
                String username = parent.getUsername();
                String password = parent.getPassword();
                String urlParameters = "chatName=" + chatName + "&creator=" + username;

                if (chatName.equals("")) {
                    createChatInfoLbl.setText("Chat name cannot be empty!");
                    return;
                }
                else if (chatName.contains(" ") || chatName.contains("&") || chatName.contains("=")) {
                    createChatInfoLbl.setText("Chat name is invalid.");
                    return;
                }

                try {
                    String response = httpClient.sendPostBasicAuthentication("/createChat", urlParameters, username, password);
                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) parser.parse(response);

                    if (jsonObject.containsKey("success")) {
                        String msg = (String) jsonObject.get("success");
                        createChatInfoLbl.setText(msg);
                        setMyChats();
                    }
                    else if (jsonObject.containsKey("error")) {
                        String msg = (String) jsonObject.get("error");
                        createChatInfoLbl.setText(msg);
                    }

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chatName = String.valueOf(myChatsComboBox.getSelectedItem());
                parent.getChat().setChannelName(chatName);
                parent.showLayout("chat");
            }
        });
        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.showLayout("login");
                Login login = parent.getLogin();
                login.clearLoginForm();
            }
        });
        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chatName = String.valueOf(invitationsComboBox.getSelectedItem());
                String username = parent.getUsername();
                String password = parent.getPassword();
                String urlParameters = "chatName=" + chatName + "&invitee=" + username;

                try {
                    String response = httpClient.sendPostBasicAuthentication("/acceptInvite", urlParameters, username, password);
                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) parser.parse(response);

                    if (jsonObject.containsKey("success")) {
                        setMyChats();
                        setInvites();
                    }

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        declineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chatName = String.valueOf(invitationsComboBox.getSelectedItem());
                String username = parent.getUsername();
                String password = parent.getPassword();
                String urlParameters = "chatName=" + chatName + "&invitee=" + username;

                try {
                    String response = httpClient.sendPostBasicAuthentication("/declineInvite", urlParameters, username, password);
                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) parser.parse(response);

                    if (jsonObject.containsKey("success")) {
                        setMyChats();
                        setInvites();
                    }

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        leaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chatName = String.valueOf(myChatsComboBox.getSelectedItem());
                String username = parent.getUsername();
                String password = parent.getPassword();
                String urlParameters = "chatName=" + chatName + "&username=" + username;

                try {
                    String response = httpClient.sendPostBasicAuthentication("/leaveChat", urlParameters, username, password);
                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) parser.parse(response);

                    if (jsonObject.containsKey("success")) {
                        setMyChats();
                        setInvites();
                    }

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    void setMyChats() {
        String username = parent.getUsername();
        String password = parent.getPassword();
        String urlParameters = "username=" + username;

        try {
            String response = httpClient.sendGetBasicAuthentication("/getMyChats", urlParameters, username, password);
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(response);

            myChatsComboBox.removeAllItems();
            if (jsonObject.containsKey("chats")) {
                JSONArray chats = (JSONArray) jsonObject.get("chats");
                Iterator iterator = chats.iterator();
                while (iterator.hasNext()) {
                    String chat = (String)iterator.next();
                    myChatsComboBox.addItem(chat);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setInvites() {
        String username = parent.getUsername();
        String password = parent.getPassword();
        String urlParameters = "username=" + username;

        try {
            String response = httpClient.sendGetBasicAuthentication("/getInvites", urlParameters, username, password);
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(response);

            invitationsComboBox.removeAllItems();
            if (jsonObject.containsKey("invitations")) {
                JSONArray chats = (JSONArray) jsonObject.get("invitations");
                Iterator iterator = chats.iterator();
                while (iterator.hasNext())
                    invitationsComboBox.addItem(iterator.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setVisible(boolean b){
        this.panel.setVisible(b);
    }

    JPanel getPanel() {
        return panel;
    }

}
