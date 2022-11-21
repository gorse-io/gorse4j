package io.gorse.gorse4j;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Score {

    private String id;
    private double score;

    @JsonProperty("Id")
    public String getId() {
        return id;
    }

    @JsonProperty("Score")
    public double getScore() {
        return score;
    }
}
