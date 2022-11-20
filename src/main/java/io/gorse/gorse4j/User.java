package io.gorse.gorse4j;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private String userId;
    private List<String> labels;

    public User() {
    }

    public User(String userId, List<String> labels) {
        this.userId = userId;
        this.labels = labels;
    }

    @JsonProperty("UserId")
    public String getUserId() {
        return userId;
    }

    @JsonProperty("Labels")
    public List<String> getLabels() {
        return labels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) && Objects.equals(labels, user.labels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, labels);
    }
}
