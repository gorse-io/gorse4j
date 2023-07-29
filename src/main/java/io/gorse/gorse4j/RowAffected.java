package io.gorse.gorse4j;

import io.avaje.jsonb.Json;

/**
 * Represents a response from the API when an action that affects a row takes place.
 * This will encapsulate the row that is affected due to a given action.
 */
@Json
public final class RowAffected {
    
    private static final long serialVersionUID = 1632935268645700L;
    
    /**
     * The affected row.
     */
    @Json.Property("RowAffected")
    private final int rowAffected;
    
    /**
     * Constructor that accepts the affected row.
     */
    public RowAffected(int rowAffected) {
        this.rowAffected = rowAffected;
    }

    public int getRowAffected() {
        return rowAffected;
    }
}