# Gorse4J

![Java Version](https://img.shields.io/badge/Java->=11-orange.svg)
[![CI](https://github.com/gorse-io/gorse4j/actions/workflows/ci.yml/badge.svg)](https://github.com/gorse-io/gorse4j/actions/workflows/ci.yml)
![Maven Central](https://img.shields.io/maven-central/v/io.gorse/gorse-client)

Java SDK for gorse recommender system

## Requirements
You must be using Java 11 or above in order to use this module.
To use it, you must add the following Maven dependencies' JARs to the module-path when running your application (click to download each):
- [`io.avaje:avaje-jsonb:1.6`](https://repo.maven.apache.org/maven2/io/avaje/avaje-jsonb/1.6/avaje-jsonb-1.6.jar)
- [`io.avaje:avaje-http-client:1.46`](https://repo.maven.apache.org/maven2/io/avaje/avaje-http-client/1.46/avaje-http-client-1.46.jar)
- [`io.avaje:avaje-http-api:1.46`](https://repo.maven.apache.org/maven2/io/avaje/avaje-http-api/1.46/avaje-http-api-1.46.jar)

This is done with, e.g. `java --module-path "example.jar:modules" --module example.module/com.example.Main`, where `example.jar` is your application JAR, `modules` is the path to folder containing all of your module JARs (so you'd put the 3 Avaje JARs in that folder), `example.module` is the name of the module of your application, and `com.example.Main` is the FQDN to your application main class.

If you are not using JPMS, you can instead just add the `gorse-client` dependency, and shade/shadow it in or manage the dependency, whichever way you see fit.

Furthermore, if you would like to use the `gorse-test` module (which uses TestContainers to setup a Gorse testing environment), you can depend on it with `test` scope (but if you are using JPMS in your tests, sadly, due to TestContainers' lack of JPMS support, you will not be able to use this module - should TestContainers add support in the future, an `Automatic-Module-Name` has already been set, so you can safely override with a newer revision of TestContainers, `requires` them both, and happily use `gorse-test` with JPMS in your tests).

## Install
Where `?` represents the desired module (`all`, `client`, `test`) - `test` requires `client` so it is advised this is used only if you are using JPMS in your application but not in your test suite.
- Install via Maven:
	```xml
	<dependency>
	    <groupId>io.gorse</groupId>
	    <artifactId>gorse-?</artifactId>
	    <version>0.4.0</version>
	</dependency>
	```
- Install via Gradle:
	```kotlin
	dependencies {
		implementation("io.gorse:gorse-?:0.4.0")
	}
	```
## Usage

```java
import io.gorse.gorse4j.*;
import io.gorse.gorse4j.model.*;

(...)

SynchronousGorseClient client = GorseFactory.synchronous(GORSE_ENDPOINT, GORSE_API_KEY);
		
client.insertFeedback(List.of(
	new Feedback("read", "100", "300", "2022-11-20T13:55:27Z"),
	new Feedback("read", "100", "400", "2022-11-20T13:55:27Z")
));
		
client.getRecommend("100");
```

# Building
When building on a Windows platform (or anywhere where GPG is not installed, *nix systems tend to bundle GPG), you should pass the `-Dgpg.skip` argument to Maven (`mvnd clean install -Dgpg.skip`).

# Contributing
Any contribution is appreciated: report a bug, give advice or create a pull request. Read [CONTRIBUTING.md](CONTRIBUTING.md) for more information.