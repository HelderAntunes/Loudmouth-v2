package client.gui;

import client.network.HttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Chat {
    private MainWindow parent;
    private HttpClient httpClient;
    private JLabel titleLbl;
    private JLabel chatNameLbl;
    private JTextArea messagesTextArea;
    private JTextField msgTextFld;
    private JButton sendMessageButton;
    private JLabel inviteSubTitleLbl;
    private JTextField inviteeTextFld;
    private JButton inviteButton;
    private JButton exitButton;
    private JPanel panel;
    private JLabel invitationInfoLbl;
    private JLabel sendMsgInfoLbl;
    private JScrollPane scrollPanel;
    private volatile boolean pollingActive = false;

    public Chat(MainWindow parent, HttpClient httpClient) {
        this.parent = parent;
        this.httpClient = httpClient;
        titleLbl.setFont (titleLbl.getFont ().deriveFont (32.0f));
        chatNameLbl.setFont (chatNameLbl.getFont ().deriveFont (16.0f));
        inviteSubTitleLbl.setFont (inviteSubTitleLbl.getFont ().deriveFont (16.0f));
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.showLayout("chats");
                stopPolling();
            }
        });
        inviteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String invitee = inviteeTextFld.getText();
                String chatName = chatNameLbl.getText();
                String username = parent.getUsername();
                String password = parent.getPassword();
                String urlParameters = "chatName=" + chatName + "&invitee=" + invitee;

                if (invitee.equals("")) {
                    invitationInfoLbl.setText("Invitee name cannot be empty!");
                    return;
                }
                else if (invitee.contains(" ") || invitee.contains("&") || invitee.contains("=")) {
                    invitationInfoLbl.setText("Invitee name is invalid.");
                    return;
                }

                try {
                    String response = httpClient.sendPostBasicAuthentication("/invitation", urlParameters, username, password);
                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) parser.parse(response);

                    if (jsonObject.containsKey("success")) {
                        String msg = (String) jsonObject.get("success");
                        invitationInfoLbl.setText(msg);
                    }
                    else if (jsonObject.containsKey("error")) {
                        String msg = (String) jsonObject.get("error");
                        invitationInfoLbl.setText(msg);
                    }

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        sendMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = msgTextFld.getText();
                String chatName = chatNameLbl.getText();
                String date = getCurrentDate();
                String username = parent.getUsername();
                String password = parent.getPassword();
                String urlParameters = "chatName=" + chatName + "&author=" + username + "&msg=" + msg + "&date=" + date;

                if (msg.equals("")) {
                    sendMsgInfoLbl.setText("Message cannot be empty!");
                    return;
                }


                try {
                    String response = httpClient.sendPostBasicAuthentication("/addMessage", urlParameters, username, password);
                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) parser.parse(response);

                    if (jsonObject.containsKey("success")) {
                        sendMsgInfoLbl.setText("");
                        setMessages(false);
                    }

                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        });
    }

    void setMessages(boolean firstTime) {
        String username = parent.getUsername();
        String password = parent.getPassword();
        String urlParameters = "chatName=" + chatNameLbl.getText();

        try {
            String response = httpClient.sendGetBasicAuthentication("/getMessages", urlParameters, username, password);
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(response);

            StringBuilder messagesTxt = new StringBuilder();
            for (Object aJsonArray : jsonArray) {
                JSONObject message = (JSONObject) aJsonArray;
                String author = (String) message.get("author");
                String msg = (String) message.get("msg");
                String date = (String) message.get("date");
                String entry = date + ":\n" + author + " said: " + msg + "\n\n";
                messagesTxt.append(entry);
            }

            messagesTextArea.setText(messagesTxt.toString());
            if (firstTime) {
                messagesTextArea.setCaretPosition(messagesTextArea.getDocument().getLength());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date now = new Date();
        return sdfDate.format(now);
    }

    void setChannelName(String chatName) {
        this.chatNameLbl.setText(chatName);
    }

    void setVisible(boolean b){
        this.panel.setVisible(b);
    }

    JPanel getPanel() {return this.panel;}

    public class GetMessagesPolling implements Runnable {

        @Override
        public void run() {
            while (pollingActive) {
                setMessages(false);
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void startPolling() {
        pollingActive = true;
        new Thread(new GetMessagesPolling()).start();
    }

    private void stopPolling() {
        pollingActive = false;
    }
}
