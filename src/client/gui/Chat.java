package client.gui;

import javax.swing.*;

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

    public Chat() {
        titleLbl.setFont (titleLbl.getFont ().deriveFont (32.0f));
        chatNameLbl.setFont (chatNameLbl.getFont ().deriveFont (16.0f));
        inviteSubTitleLbl.setFont (inviteSubTitleLbl.getFont ().deriveFont (16.0f));
    }
}
