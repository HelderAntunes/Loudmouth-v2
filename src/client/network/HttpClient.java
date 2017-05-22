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

    //  String urlParameters = "username=" + username + "&password=" + password;
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

    private String getSuccessMsg(String response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObj = (JSONObject)parser.parse(response);

        if (jsonObj.containsKey("success"))
            return "Success";
        else if (jsonObj.containsKey("error"))
            return "Error: " + jsonObj.get("msg");
        return "Error.";
    }

   /* public void sendGet() throws Exception {

        String url = "http://localhost:8000/get?hello=word&foo=bar";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONParser parser = new JSONParser();
        JSONObject jsonObj = (JSONObject)parser.parse(response.toString());
        System.out.println(jsonObj.get("hello") + " " + jsonObj.get("foo"));

    }

    public void sendPost() throws Exception {

        byte[] loginBytes = ("admin" + ":" + "password").getBytes();

        StringBuilder loginBuilder = new StringBuilder()
                .append("Basic ")
                .append(new String(Base64.getEncoder().encode(loginBytes)));

        String url = "http://localhost:8000/post";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.addRequestProperty("Authorization", loginBuilder.toString());

        //add request header
        con.setRequestMethod("POST");

        String urlParameters = "hello=word&foo=bar";

        // Send post request
        con.setDoOutput(true);
        con.setDoInput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONParser parser = new JSONParser();
        JSONObject jsonObj = (JSONObject)parser.parse(response.toString());
        System.out.println(jsonObj.get("hello") + " " + jsonObj.get("foo"));

    }*/
}
