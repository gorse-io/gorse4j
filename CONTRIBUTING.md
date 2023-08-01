# Contribution Guide

Welcome and thank you for considering contributing to Gorse4J!

Reading and following these guidelines will help us make the contribution process easy and effective for everyone involved. It also communicates that you agree to respect the time of the developers managing and developing these open source projects. In return, we will reciprocate that respect by addressing your issue, assessing changes, and helping you finalize your pull requests.

## Getting Started

### Setup Develop Environment

These following installations are required:

- **Java 11+**: Gorse4J is written in Java 11.
- **Maven**: Gorse4J uses Maven to manage dependencies and build the project.
- **Docker Compose**: Start a local Gorse cluster for testing.

```bash
docker-compose up -d
```

### Run Unit Tests

```bash
mvn test
```

## Your First Contribution

### Contribution Workflow

To contribute to the Gorse code base, please follow the workflow as defined in this section.

- Fork the repository to your own Github account
- Make commits and add test case if the change fixes a bug or adds new functionality.
- Run tests and make sure all the tests are passed.
- Push your changes to a topic branch in your fork of the repository.
- Submit a pull request.

This is a rough outline of what a contributor's workflow looks like. Thanks for your contributions!

## Getting Help

Join us in the [Discord](https://discord.gg/x6gAtNNkAE) and post your question in the `#developers` channel.
