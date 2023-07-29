# Gorse4J

![Java Version](https://img.shields.io/badge/Java->=11-orange.svg)
[![CI](https://github.com/gorse-io/gorse4j/actions/workflows/ci.yml/badge.svg)](https://github.com/gorse-io/gorse4j/actions/workflows/ci.yml)
![Maven Central](https://img.shields.io/maven-central/v/io.gorse/gorse-client)

Java SDK for gorse recommender system

## Requirements
You must be using Java 11 or above in order to use this module.

## Install
- Install via Maven:
```xml
<dependency>
    <groupId>io.gorse</groupId>
    <artifactId>gorse-client</artifactId>
    <version>0.4.0</version>
</dependency>
```
- Install via Gradle:
```kotlin
dependencies {
	implementation("io.gorse:gorse-client:0.4.0")
}
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

# Building
When building on a Windows platform (or anywhere where GPG is not installed, *nix systems tend to bundle GPG), you should pass the `-Dgpg.skip` argument to Maven (`mvnd clean install -Dgpg.skip`).

# Contributing
Any contribution is appreciated: report a bug, give advice or create a pull request. Read [CONTRIBUTING.md](https://github.com/gorse-io/gorse/blob/master/CONTRIBUTING.md) for more information.