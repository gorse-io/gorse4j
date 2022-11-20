package io.gorse.gorse4j;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Gorse {

    private final String endpoint;
    private final String apiKey;

    public Gorse(String endpoint, String apiKey) {
        this.endpoint = endpoint;
        this.apiKey = apiKey;
    }

    public RowAffected insertUser(User user) throws IOException {
        return this.Do("POST", this.endpoint + "/api/user", user, RowAffected.class);
    }

    public User getUser(String userId) throws IOException {
        return this.Do("GET", this.endpoint + "/api/user/" + userId, null, User.class);
    }

    public RowAffected deleteUser(String userId) throws IOException {
        return this.Do("DELETE", this.endpoint + "/api/user/" + userId, null, RowAffected.class);
    }

    private <Request, Response> Response Do(String method, String url, Request request, Class<Response> responseClass) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("X-API-Key", this.apiKey);
        connection.setRequestProperty("Content-Type", "application/json");
        // Send request
        ObjectMapper mapper = new ObjectMapper();
        if (request != null) {
            connection.setDoOutput(true);
            String requestBody = mapper.writeValueAsString(request);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.write(requestBody.getBytes());
            outputStream.close();
        }
        // Get Response
        InputStream inputStream = connection.getInputStream();
        return mapper.readValue(inputStream, responseClass);
    }
}
