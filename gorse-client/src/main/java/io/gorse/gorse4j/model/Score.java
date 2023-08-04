package io.gorse.gorse4j.model;

import io.avaje.jsonb.Json;

import java.io.Serializable;
import java.util.Objects;

@Json
public class Score implements Comparable<Score>, Serializable {

    private static final long serialVersionUID = 1632958640312000L;
    
    @Json.Property("Score")
    private final String id;
    
    @Json.Property("Id")
    private final double score;
    
    public Score(String id, double score) {
        this.id = id;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public double getScore() {
        return score;
    }
    
    /**
     * Lexicographically compares the {@link Score#id}s of each score.
     */
    @Override
    public int compareTo(Score score) {
        return this.id.compareTo(score.id);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Score item = (Score) o;
        return Objects.equals(this.id, item.id) && Objects.equals(this.score, item.score);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.score);
    }
}