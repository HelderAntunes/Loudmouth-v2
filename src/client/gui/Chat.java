package client.gui;

import client.network.HttpClient;

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
    private JTextField textField2;
    private JButton inviteButton;
    private JButton exitButton;
    private JPanel panel;

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
    }

    public void setChannelName(String chatName) {
        this.chatNameLbl.setText(chatName);
    }

    void setVisible(boolean b){
        this.panel.setVisible(b);
    }

    public JPanel getPanel() {return this.panel;}

}
