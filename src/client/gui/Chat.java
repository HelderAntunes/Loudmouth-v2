package client.gui;

import client.network.HttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Chat {
    private JLabel titleLbl;
    private JLabel chatNameLbl;
    private JTextArea messagesTextArea;
    private JTextField textField1;
    private JButton sendMessageButton;
    private JLabel inviteSubTitleLbl;
    private JTextField inviteeTextFld;
    private JButton inviteButton;
    private JButton exitButton;
    private JPanel panel;
    private JLabel invitationInfoLbl;

    public Chat(MainWindow parent, HttpClient httpClient) {
        titleLbl.setFont (titleLbl.getFont ().deriveFont (32.0f));
        chatNameLbl.setFont (chatNameLbl.getFont ().deriveFont (16.0f));
        inviteSubTitleLbl.setFont (inviteSubTitleLbl.getFont ().deriveFont (16.0f));
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.showLayout("chats");
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
    }

    public void setChannelName(String chatName) {
        this.chatNameLbl.setText(chatName);
    }

    void setVisible(boolean b){
        this.panel.setVisible(b);
    }

    public JPanel getPanel() {return this.panel;}

}
