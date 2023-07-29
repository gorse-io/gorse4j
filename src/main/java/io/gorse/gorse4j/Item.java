package io.gorse.gorse4j;

import io.avaje.jsonb.Json;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Represents an item in the recommender system.
 * Implements {@link Comparable}, lexicographically comparing the item's ID.
 */
@Json
public class Item implements Comparable<Item>, Serializable {
    
    private static final long serialVersionUID = 1632919391573100L;
    
    /**
     * The unique identifier of the item. Cannot contain a slash "/"
     * due to conflicts with the URL definition of the RESTful APIs.
     */
    @Json.Property("ItemId")
    private final String itemId;
    
    /**
     * Whether the item is hidden - after setting true, the item will
     * no longer appear in the recommendation results.
     */
    @Json.Property("IsHidden")
    private final Boolean isHidden;
    
    /**
     * The item's label information, which is used to describe the
     * item's characteristics to the recommender system.
     */
    @Json.Property("Labels")
    private final List<String> labels;
    
    /**
     * The categories to which the item belongs - the item is recommended
     * under these categories.
     */
    @Json.Property("Categories")
    private final List<String> categories;
    
    /**
     * The timestamp of the item, which is used to determine the "freshness"
     * of the item in the recommender system.
     */
    @Json.Property("Timestamp")
    private final String timestamp;
    
    /**
     * The item's comment information, which helps to browse items and
     * recommendation results in the dashboard.
     */
    @Json.Property("Comment")
    private final String comment;
    
    /**
     * Constructor for an item accepting a unique identifier, whether the item
     * should be visible in the recommender results, label information, categories
     * in which the item belongs, the timestamp of the item used to determine the
     * "freshness" of the item, and comment information for the dashboard.
     */
    public Item(String itemId, Boolean isHidden, List<String> labels, List<String> categories, String timestamp, String comment) {
        this.itemId = itemId;
        this.isHidden = isHidden;
        this.labels = labels;
        this.categories = categories;
        this.timestamp = timestamp;
        this.comment = comment;
    }

    public String getItemId() {
        return itemId;
    }
    
    /**
     * @deprecated
     * @see Item#isHidden()
     */
    @Deprecated
    public Boolean getIsHidden() {
        return isHidden;
    }
    
    public Boolean isHidden() {
        return isHidden;
    }

    public List<String> getLabels() {
        return labels;
    }

    public List<String> getCategories() {
        return categories;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getComment() {
        return comment;
    }
    
    /**
     * Lexicographically compares the {@link Item#itemId} of each item.
     */
    @Override
    public int compareTo(Item item) {
        return this.itemId.compareTo(item.itemId);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(this.itemId, item.itemId) && Objects.equals(this.isHidden, item.isHidden) && Objects.equals(this.labels, item.labels) && Objects.equals(this.categories, item.categories) && Objects.equals(this.timestamp, item.timestamp) && Objects.equals(this.comment, item.comment);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.itemId, this.isHidden, this.labels, this.categories, this.timestamp, this.comment);
    }
}