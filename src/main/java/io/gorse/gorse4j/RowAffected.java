package io.gorse.gorse4j;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RowAffected {

    private int rowAffected;

    public RowAffected() {
    }

    public RowAffected(int rowAffected) {
        this.rowAffected = rowAffected;
    }

    @JsonProperty("RowAffected")
    public int getRowAffected() {
        return rowAffected;
    }
}
