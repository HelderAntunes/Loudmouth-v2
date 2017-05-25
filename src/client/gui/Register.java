package client.gui;

import client.network.HttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
    private JLabel errorLbl;
    private JLabel titleLbl;
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
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameTextField.getText();
                String password = passwordPasswordField.getText();
                String repeatPassword = repeatPasswordPasswordField.getText();

                if (username.equals("")) {
                    errorLbl.setText("username cannot be empty!");
                    return;
                }
                else if (username.contains(" ") || username.contains("&") || username.contains("=")) {
                    errorLbl.setText("username is invalid.");
                    return;
                }

                if (!password.equals(repeatPassword)) {
                    errorLbl.setText("Passwords do not match!");
                    return;
                }

                errorLbl.setText("");
                String urlParameters = "username=" + username + "&password=" + password;
                try {
                    String response = httpClient.sendPost("/register", urlParameters);
                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) parser.parse(response);

                    if (jsonObject.containsKey("success")) {
                        String msg = (String) jsonObject.get("success");
                        errorLbl.setText(msg);
                        parent.setUsername(username);
                        parent.setPassword(password);
                        parent.showLayout("chats");
                    }
                    else if (jsonObject.containsKey("error")) {
                        String msg = (String) jsonObject.get("error");
                        errorLbl.setText(msg);
                    }

                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        });

        titleLbl.setFont (titleLbl.getFont ().deriveFont (32.0f));

    }

    void setVisible(boolean b){
        this.panel.setVisible(b);
    }

    JPanel getPanel() {
        return panel;
    }

    void clearRegisterForm() {
        errorLbl.setText("");
        usernameTextField.setText("");
        passwordPasswordField.setText("");
        repeatPasswordPasswordField.setText("");
    }

}
