# Gorse4J

[![CI](https://github.com/gorse-io/gorse4j/actions/workflows/ci.yml/badge.svg)](https://github.com/gorse-io/gorse4j/actions/workflows/ci.yml)
![Maven Central](https://img.shields.io/maven-central/v/io.gorse/gorse-client)

Java SDK for gorse recommender system

## Install

```xml
<dependency>
    <groupId>io.gorse</groupId>
    <artifactId>gorse-client</artifactId>
    <version>0.4.0</version>
</dependency>
```

## Usage

```java
import io.gorse.gorse4j.*;

public class Main {

    public static void main(String[] args) {
        Gorse client = new Gorse(GORSE_ENDPOINT, GORSE_API_KEY);

        List<Feedback> feedbacks = List.of(
                new Feedback("read", "100", "300", "2022-11-20T13:55:27Z"),
                new Feedback("read", "100", "400", "2022-11-20T13:55:27Z")
        );
        client.insertFeedback(feedbacks);

        client.getRecommend("100");
    }
}
```
