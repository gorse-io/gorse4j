# Gorse4J

[![CI](https://github.com/gorse-io/gorse4j/actions/workflows/ci.yml/badge.svg)](https://github.com/gorse-io/gorse4j/actions/workflows/ci.yml)
![Maven Central](https://img.shields.io/maven-central/v/io.gorse/gorse-client)

Java SDK for gorse recommender system

## Install

```xml
<dependency>
    <groupId>io.gorse</groupId>
    <artifactId>gorse-client</artifactId>
    <version>0.5.0</version>
</dependency>
```

## Usage

```java
import io.gorse.gorse4j.*;
import java.util.LinkedHashMap;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        Gorse client = new Gorse(GORSE_ENDPOINT, GORSE_API_KEY);

        // Insert a user
        User user = new User("100", new LinkedHashMap<String, String>() {{
            put("gender", "M");
            put("occupation", "engineer");
        }});
        client.insertUser(user);

        // Insert a item
        Item item = new Item("300", true, new LinkedHashMap<String, Object>() {{
            put("genre", "Comedy");
        }}, List.of("movie", "comedy"), "2022-11-20T00:00:00Z", "Funny Movie");
        client.insertItem(item);

        // Insert feedback
        List<Feedback> feedbacks = List.of(
                new Feedback("read", "100", "300", 1, "2022-11-20T13:55:27Z"),
                new Feedback("read", "100", "400", 2, "2022-11-20T13:55:27Z")
        );
        client.insertFeedback(feedbacks);

        // Get recommendations
        client.getRecommend("100");
    }
}
```
