import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.io.IOException;

import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;

public class ConnectionManager {

    private static String connectionString = "http://127.0.0.1:8000/BackApp/";

    public static void readUtenti() {
        try {
            URL url = new URL (connectionString + "readUtenti/");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();

            if(responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String inputLine;

                StringBuffer response = new StringBuffer();

                while((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                Gson gson = new Gson();
                String json = gson.toJson(response);

                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(json, new TypeReference<Map<String, Object>>(){});

                System.out.println(map.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } 
    }

    public static int login(String email, String password) throws Exception {

        try {

            URL url = new URL(connectionString + "login?utente_email=" + email + "&utente_password=" + password);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            if (connection.getResponseCode() == 200) {

                String inputLine = "";
                StringBuffer response = new StringBuffer();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                while((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                if (response.length() > 0) {

                    ObjectMapper mapper = new ObjectMapper();
                    HashMap<String, Object> map = mapper.readValue(response.toString(), new TypeReference<HashMap<String, Object>>() {});

                    return (Integer) map.get("utente_id");

                } else {
                    throw new Exception("Email e/o password non corretti.");
                }

            } else {
                throw new Exception("Errore nella connessione al server");
            }

        }
        catch (IOException e) {
            throw new Exception("Errore nella connessione al server");
        }
    }
}