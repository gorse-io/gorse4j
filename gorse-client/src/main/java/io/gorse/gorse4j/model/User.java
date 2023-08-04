package io.gorse.gorse4j.model;

import io.avaje.jsonb.Json;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Represents a user in the recommender system.
 * Implements {@link Comparable}, lexicographically comparing the user's ID.
 */
@Json
public class User implements Comparable<User>, Serializable {
    
    /**
     * The unique identifier of the user. Cannot contain a slash "/"
     * due to conflicts with the URL definition of the RESTful APIs.
     */
    @Json.Property("UserId")
    private final String userId;
    
    /**
     * The user's label information, which is used to describe
     * the user's characteristics to the recommender system.
     */
    @Json.Property("Labels")
    private final List<String> labels;
    
    /**
     * Constructor for a user accepting their identifier and a list of label information.
     */
    public User(String userId, List<String> labels) {
        this.userId = userId;
        this.labels = labels;
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getLabels() {
        return labels;
    }
    
    /**
     * Lexicographically compares the {@link User#userId} of each user.
     */
    @Override
    public int compareTo(User user) {
        return this.userId.compareTo(user.userId);
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