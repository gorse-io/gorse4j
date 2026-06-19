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

## Kafka Connect Sink

The `gorse-kafka-connect` module provides a Kafka Connect sink connector for
streaming users, items, and feedback from Kafka topics into Gorse.

```properties
name=gorse-sink
connector.class=io.gorse.gorse4j.connect.GorseSinkConnector
tasks.max=1
topics=gorse-users,gorse-items,gorse-feedback

gorse.endpoint=http://gorse-server:8088
gorse.api.key=your-api-key
gorse.batch.size=500

topic.gorse-users.entity=user
topic.gorse-items.entity=item
topic.gorse-feedback.entity=feedback

topic.gorse-feedback.field.feedback_type=event_type
topic.gorse-feedback.field.user_id=uid
topic.gorse-feedback.field.item_id=iid
topic.gorse-feedback.field.timestamp=created_at
topic.gorse-feedback.field.value=score
```

If the Kafka message already uses Gorse field names such as `UserId`, `ItemId`,
`FeedbackType`, `Labels`, `Categories`, `Timestamp`, and `Comment`, field mapping
configuration is optional. The connector also accepts nested paths like
`user.id` and supports JSON string, schemaless map, and Kafka Connect `Struct`
record values.
