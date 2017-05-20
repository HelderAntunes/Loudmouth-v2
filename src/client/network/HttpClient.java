package client.network;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {

    public static void main(String[] args) throws Exception {

        HttpClient http = new HttpClient();
        //http.sendGet();
        http.sendPost();
    }

    public void sendGet() throws Exception {

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

        String url = "http://localhost:8000/post";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

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

    }
}
