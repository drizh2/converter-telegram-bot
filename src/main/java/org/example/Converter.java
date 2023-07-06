package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Converter {

    public String convert(String source, String target, int amount) throws IOException {
        URL url = new URL("https://api.api-ninjas.com/v1/convertcurrency?want=" + target + "&have=" + source + "&amount=" + amount);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("accept", "application/json");
        InputStream responseStream = connection.getInputStream();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(responseStream);
        return root.path("new_amount").asText();
    }


}
