## File mavenArtifact<>(String groupId, String artifactId, String version)

Downloads a Java library from Maven Central repository.
__Note__: Snapshot versions are downloaded once and cached.
They are not redownloaded upon later invocations.
This will be improved in the future.

| Name       | Type   | Default | Description                                                |
|------------|--------|---------|------------------------------------------------------------|
| groupId    | String |         | Group ID of the Maven artifact (e.g., "org.junit.jupiter") |
| artifactId | String |         | Artifact ID of the Maven artifact (e.g., "junit-jupiter")  |
| version    | String |         | Version of the Maven artifact (e.g., "5.8.1")              |

Returns __File__ containing the downloaded JAR file.

### examples

Download JUnit Jupiter API:
```
File junit = mavenArtifact("org.junit.jupiter", "junit-jupiter-api", "5.8.1");
```

Download Google Guava:
```
File guava = mavenArtifact("com.google.guava", "guava", "31.1-jre");
```
