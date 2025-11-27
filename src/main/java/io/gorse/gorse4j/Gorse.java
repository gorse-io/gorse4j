package io.gorse.gorse4j;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Gorse {

    private final String endpoint;
    private final String apiKey;

    public Gorse(String endpoint, String apiKey) {
        this.endpoint = endpoint;
        this.apiKey = apiKey;
    }

    public RowAffected insertUser(User user) throws IOException {
        return this.request("POST", this.endpoint + "/api/user", user, RowAffected.class);
    }

    public User getUser(String userId) throws IOException {
        return this.request("GET", this.endpoint + "/api/user/" + userId, null, User.class);
    }

    public RowAffected deleteUser(String userId) throws IOException {
        return this.request("DELETE", this.endpoint + "/api/user/" + userId, null, RowAffected.class);
    }

    public RowAffected insertItem(Item item) throws IOException {
        return this.request("POST", this.endpoint + "/api/item", item, RowAffected.class);
    }

    public Item getItem(String itemId) throws IOException {
        return this.request("GET", this.endpoint + "/api/item/" + itemId, null, Item.class);
    }

    public RowAffected deleteItem(String itemId) throws IOException {
        return this.request("DELETE", this.endpoint + "/api/item/" + itemId, null, RowAffected.class);
    }

    public RowAffected insertFeedback(List<Feedback> feedbacks) throws IOException {
        return this.request("POST", this.endpoint + "/api/feedback", feedbacks, RowAffected.class);
    }

    public RowAffected deleteFeedback(String feedbackType, String userId, String itemId) throws IOException {
        return this.request("DELETE", this.endpoint + "/api/feedback/" + feedbackType + "/" + userId + "/" + itemId, null, RowAffected.class);
    }

    public List<Feedback> listFeedback(String userId, String feedbackType) throws IOException {
        return List.of(this.request("GET", this.endpoint + "/api/user/" + userId + "/feedback/" + feedbackType, null, Feedback[].class));
    }

    public List<String> getRecommend(String userId) throws IOException {
        return List.of(this.request("GET", this.endpoint + "/api/recommend/" + userId, null, String[].class));
    }

    private <Request, Response> Response request(String method, String url, Request request, Class<Response> responseClass) throws IOException {
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
