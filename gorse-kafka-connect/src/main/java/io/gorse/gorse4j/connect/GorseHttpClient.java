package io.gorse.gorse4j.connect;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

final class GorseHttpClient {

    private final String endpoint;
    private final String apiKey;
    private final ObjectMapper mapper = new ObjectMapper();

    GorseHttpClient(String endpoint, String apiKey) {
        this.endpoint = endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
        this.apiKey = apiKey;
    }

    void insertUsers(List<Map<String, Object>> users) throws IOException {
        post("/api/users", users);
    }

    void insertItems(List<Map<String, Object>> items) throws IOException {
        post("/api/items", items);
    }

    void insertFeedback(List<Map<String, Object>> feedback) throws IOException {
        post("/api/feedback", feedback);
    }

    private void post(String path, Object body) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(endpoint + path).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("X-API-Key", apiKey);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        byte[] requestBody = mapper.writeValueAsString(body).getBytes(StandardCharsets.UTF_8);
        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
            outputStream.write(requestBody);
        }
        int status = connection.getResponseCode();
        if (status < 200 || status >= 300) {
            throw new IOException("Gorse request failed with status " + status + ": " + responseBody(connection));
        }
        try (InputStream inputStream = connection.getInputStream()) {
            while (inputStream.read() != -1) {
                // Drain the response so the underlying HTTP connection can be reused by the JVM.
            }
        }
    }

    private static String responseBody(HttpURLConnection connection) throws IOException {
        InputStream errorStream = connection.getErrorStream();
        if (errorStream == null) {
            return "";
        }
        try (InputStream inputStream = errorStream) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
