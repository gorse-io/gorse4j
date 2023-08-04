package io.gorse.gorse4j.model;

import io.avaje.jsonb.Json;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * Represents feedback in the recommender system.
 * Feedback represents events that happened between users and items, which can be positive or negative.
 * Implements {@link Comparable}, lexicographically comparing the timestamp.
 */
@Json
public class Feedback implements Comparable<Feedback>, Serializable {
    
    private static final long serialVersionUID = 1584197740135000L;
    
    /**
     * The type of feedback this represents.
     */
    @Json.Property("FeedbackType")
    private final String feedbackType;
    
    /**
     * The unique identifier of the user this feedback is regarding.
     * Cannot contain a slash "/" due to conflicts with the URL definition
     * of the RESTful APIs.
     */
    @Json.Property("UserId")
    private final String userId;
    
    /**
     * The unique identifier of the item this feedback is regarding.
     * @see Item#getItemId()
     */
    @Json.Property("ItemId")
    private final String itemId;
    
    /**
     * The timestamp of the feedback, which is used to determine the "freshness"
     * of the feedback in the recommender system.
     */
    @Json.Property("Timestamp")
    private final Instant timestamp;
    
    public Feedback(String feedbackType, String userId, String itemId, Instant timestamp) {
        this.feedbackType = feedbackType;
        this.userId = userId;
        this.itemId = itemId;
        this.timestamp = timestamp;
    }

    public String getFeedbackType() {
        return feedbackType;
    }

    public String getUserId() {
        return userId;
    }

    public String getItemId() {
        return itemId;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    /**
     * Lexicographically compares the {@link Feedback#timestamp} of each feedback.
     */
    @Override
    public int compareTo(Feedback feedback) {
        return this.timestamp.compareTo(feedback.timestamp);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feedback feedback = (Feedback) o;
        return Objects.equals(feedbackType, feedback.feedbackType) && Objects.equals(userId, feedback.userId) && Objects.equals(itemId, feedback.itemId) && Objects.equals(timestamp, feedback.timestamp);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(feedbackType, userId, itemId, timestamp);
    }
}