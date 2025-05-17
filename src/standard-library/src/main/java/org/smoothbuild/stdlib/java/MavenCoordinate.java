package org.smoothbuild.stdlib.java;

import static java.util.Objects.requireNonNull;

public record MavenCoordinate(String groupId, String artifactId, String version) {
  private static final String MAVEN_CENTRAL_URL = "https://repo1.maven.org/maven2";

  public MavenCoordinate {
    requireNonNull(groupId, "groupId cannot be null");
    requireNonNull(artifactId, "artifactId cannot be null");
    requireNonNull(version, "version cannot be null");
  }

  public String url() {
    var groupIdPath = groupId.replace('.', '/');
    return MAVEN_CENTRAL_URL + "/" + groupIdPath + "/" + artifactId + "/" + version + "/"
        + jarName();
  }

  public String jarName() {
    return artifactId + "-" + version + ".jar";
  }

  @Override
  public String toString() {
    return groupId + ":" + artifactId + ":" + version;
  }
}
