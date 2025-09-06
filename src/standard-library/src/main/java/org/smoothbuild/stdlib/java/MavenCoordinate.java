package org.smoothbuild.stdlib.java;

public record MavenCoordinate(String groupId, String artifactId, String version) {
  private static final String MAVEN_CENTRAL_URL = "https://repo1.maven.org/maven2";

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
