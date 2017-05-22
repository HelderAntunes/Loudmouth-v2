package client.gui;

import client.network.HttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
    private JLabel errorLbl;
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
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameTextField.getText();
                String password = passwordPasswordField.getText();

                errorLbl.setText("");
                String urlParameters = "username=" + username + "&password=" + password;
                try {
                    String response = httpClient.sendPost("/login", urlParameters);
                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) parser.parse(response);

                    if (jsonObject.containsKey("success")) {
                        String msg = (String) jsonObject.get("success");
                        errorLbl.setText(msg);
                        parent.setUsername(username);
                        parent.setPassword(password);
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
    }

    void setVisible(boolean b){
        this.panel.setVisible(b);
    }

    JPanel getPanel() {
        return panel;
    }

}
