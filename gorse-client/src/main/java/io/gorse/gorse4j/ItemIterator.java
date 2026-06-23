package io.gorse.gorse4j;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class ItemIterator {

    private String cursor;
    private List<Item> items;

    public ItemIterator() {
    }

    public ItemIterator(String cursor, List<Item> items) {
        this.cursor = cursor;
        this.items = items;
    }

    @JsonProperty("Cursor")
    public String getCursor() {
        return cursor;
    }

    @JsonProperty("Items")
    public List<Item> getItems() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemIterator that = (ItemIterator) o;
        return Objects.equals(cursor, that.cursor) && Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cursor, items);
    }
}
