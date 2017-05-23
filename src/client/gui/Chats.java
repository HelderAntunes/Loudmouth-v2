package client.gui;

import client.network.HttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Chats {
    private MainWindow parent;
    private HttpClient httpClient;
    private JLabel titleLbl;
    private JLabel myChatsTitleLbl;
    private JLabel myInvitationsLbl;
    private JLabel createChatLbl;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
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
        myChatsTitleLbl.setFont (titleLbl.getFont ().deriveFont (16.0f));
        myInvitationsLbl.setFont (titleLbl.getFont ().deriveFont (16.0f));
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

                try {
                    String response = httpClient.sendPostBasicAuthentication("/createChat", urlParameters, username, password);
                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) parser.parse(response);

                    if (jsonObject.containsKey("success")) {
                        String msg = (String) jsonObject.get("success");
                        createChatInfoLbl.setText(msg);
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
    }

    void setVisible(boolean b){
        this.panel.setVisible(b);
    }

    JPanel getPanel() {
        return panel;
    }

}
