package io.gorse.gorse4j;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Feedback {

    private String feedbackType;
    private String userId;
    private String itemId;
    private double value;
    private String timestamp;
    private Object labels;
    private String comment;

    public Feedback() {
    }

    public Feedback(String feedbackType, String userId, String itemId, double value, String timestamp) {
        this.feedbackType = feedbackType;
        this.userId = userId;
        this.itemId = itemId;
        this.value = value;
        this.timestamp = timestamp;
    }

    public Feedback(String feedbackType, String userId, String itemId, double value, String timestamp, Object labels, String comment) {
        this.feedbackType = feedbackType;
        this.userId = userId;
        this.itemId = itemId;
        this.value = value;
        this.timestamp = timestamp;
        this.labels = labels;
        this.comment = comment;
    }

    @JsonProperty("FeedbackType")
    public String getFeedbackType() {
        return feedbackType;
    }

    @JsonProperty("UserId")
    public String getUserId() {
        return userId;
    }

    @JsonProperty("ItemId")
    public String getItemId() {
        return itemId;
    }

    @JsonProperty("Value")
    public double getValue() {
        return value;
    }

    @JsonProperty("Timestamp")
    public String getTimestamp() {
        return timestamp;
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
