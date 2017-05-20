package client.gui;

import client.network.HttpClient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Register {
    private MainWindow parent;
    private JPanel panel;
    private JPasswordField passwordPasswordField;
    private JPasswordField repeatPasswordPasswordField;
    private JTextField usernameTextField;
    private JButton registerButton;
    private JButton alreadyRegisteredPressHereButton;
    private HttpClient httpClient;

    public Register(MainWindow parent, HttpClient httpClient) {
        this.parent = parent;
        this.httpClient = httpClient;
        alreadyRegisteredPressHereButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.showLayout("login");
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
