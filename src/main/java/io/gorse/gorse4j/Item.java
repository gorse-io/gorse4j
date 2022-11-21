package io.gorse.gorse4j;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class Item {

    private String itemId;
    private Boolean isHidden;
    private List<String> labels;
    private List<String> categories;
    private String timestamp;
    private String comment;

    public Item() {
    }

    public Item(String itemId, Boolean isHidden, List<String> labels, List<String> categories, String timestamp, String comment) {
        this.itemId = itemId;
        this.isHidden = isHidden;
        this.labels = labels;
        this.categories = categories;
        this.timestamp = timestamp;
        this.comment = comment;
    }

    @JsonProperty("ItemId")
    public String getItemId() {
        return itemId;
    }

    @JsonProperty("IsHidden")
    public Boolean getIsHidden() {
        return isHidden;
    }

    @JsonProperty("Labels")
    public List<String> getLabels() {
        return labels;
    }

    @JsonProperty("Categories")
    public List<String> getCategories() {
        return categories;
    }

    @JsonProperty("Timestamp")
    public String getTimestamp() {
        return timestamp;
    }

    @JsonProperty("Comment")
    public String getComment() {
        return comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(itemId, item.itemId) && Objects.equals(isHidden, item.isHidden) && Objects.equals(labels, item.labels) && Objects.equals(categories, item.categories) && Objects.equals(timestamp, item.timestamp) && Objects.equals(comment, item.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, isHidden, labels, categories, timestamp, comment);
    }
}
