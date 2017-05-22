package client.gui;

import client.network.HttpClient;

import javax.swing.*;

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
    private JTextField textField1;
    private JPanel panel;
    private JButton logOutButton;

    public Chats(MainWindow parent, HttpClient httpClient) {
        this.parent = parent;
        this.httpClient = httpClient;
        titleLbl.setFont (titleLbl.getFont ().deriveFont (32.0f));
        myChatsTitleLbl.setFont (titleLbl.getFont ().deriveFont (16.0f));
        myInvitationsLbl.setFont (titleLbl.getFont ().deriveFont (16.0f));
        createChatLbl.setFont (titleLbl.getFont ().deriveFont (16.0f));
    }

    void setVisible(boolean b){
        this.panel.setVisible(b);
    }

    JPanel getPanel() {
        return panel;
    }

}
