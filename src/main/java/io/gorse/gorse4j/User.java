package io.gorse.gorse4j;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private String userId;
    private Object labels;
    private String comment;

    public User() {
    }

    public User(String userId, Object labels) {
        this.userId = userId;
        this.labels = labels;
    }

    @JsonProperty("UserId")
    public String getUserId() {
        return userId;
    }

    @JsonProperty("Labels")
    public Object getLabels() {
        return labels;
    }

    @JsonProperty("Comment")
    public String getComment() {
        return comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return userId.equals(user.userId) &&
                labels.equals(user.labels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, labels);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", labels=" + labels +
                ", comment='" + comment + '\'' +
                '}';
    }
}
