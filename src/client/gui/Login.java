package client.gui;

import client.network.HttpClient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login {
    private MainWindow parent;
    private JPanel panel;
    private JTextField usernameTextField;
    private JPasswordField passwordPasswordField;
    private JButton loginButton;
    private JButton notRegisteredPressHereButton;
    private HttpClient httpClient;

    public Login(MainWindow parent, HttpClient httpClient) {
        this.parent = parent;
        this.httpClient = httpClient;
        notRegisteredPressHereButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.showLayout("register");
            }
        });
    }

    public void setVisible(boolean b){
        this.panel.setVisible(b);
    }

    public JPanel getPanel() {
        return panel;
    }

}
