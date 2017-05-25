package client.network;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Iterator;
import java.util.Set;

public class HttpClient {

    private final String base_url = "http://localhost:8000";

    public static void main(String[] args) throws Exception {
        HttpClient httpClient = new HttpClient();
        String registerResponse = httpClient.sendPost("/login", "username=helder&password=123456789");
        System.out.println(registerResponse);
    }

    public String sendGet(String path, String urlParameters) throws Exception {
        String url = base_url + path + "?" + urlParameters;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        if (responseCode != 200)
            return "Error: code " + responseCode + ".";

        return readResponse(con);
    }

    public String sendPost(String path, String urlParameters) throws Exception {
        String url = base_url + path;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setDoInput(true);
        setPostParameters(con, urlParameters);

        int responseCode = con.getResponseCode();
        if (responseCode != 200)
            return "Error: code " + responseCode + ".";

        return readResponse(con);
    }

    public  String sendPostBasicAuthentication(String path, String urlParameters, String username, String password) throws Exception {
        String url = base_url + path;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        String authorizationEncoded = getAuthorizationStringEncoded(username, password);

        con.addRequestProperty("Authorization", authorizationEncoded);
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setDoInput(true);
        setPostParameters(con, urlParameters);

        int responseCode = con.getResponseCode();
        if (responseCode != 200)
            return "Error: code " + responseCode + ".";

        return readResponse(con);
    }

    public  String sendGetBasicAuthentication(String path, String urlParameters, String username, String password) throws Exception {
        String url = base_url + path + "?" + urlParameters;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        String authorizationEncoded = getAuthorizationStringEncoded(username, password);

        con.addRequestProperty("Authorization", authorizationEncoded);
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        if (responseCode != 200)
            return "Error: code " + responseCode + ".";

        return readResponse(con);
    }

    private String getAuthorizationStringEncoded(String username, String password) {
        byte[] authBytes = (username + ":" + password).getBytes();
        StringBuilder authBuilder = new StringBuilder()
                .append("Basic ")
                .append(new String(Base64.getEncoder().encode(authBytes)));
        return authBuilder.toString();
    }

    private void setPostParameters(HttpURLConnection con, String postParameters) throws IOException {
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(postParameters);
        wr.flush();
        wr.close();
    }

    private String readResponse(HttpURLConnection con) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}
