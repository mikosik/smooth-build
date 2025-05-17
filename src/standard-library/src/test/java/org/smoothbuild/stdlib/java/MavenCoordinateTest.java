package org.smoothbuild.stdlib.java;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MavenCoordinateTest {
  @Test
  void to_string() {
    var mavenCoordinate = new MavenCoordinate("com.example", "example", "1.0.0");
    assertThat(mavenCoordinate.toString()).isEqualTo("com.example:example:1.0.0");
  }

  @Nested
  class _url {
    @Test
    void with_simple_groupId() {
      var mavenCoordinate = new MavenCoordinate("group", "artifact", "1.0.0");
      assertThat(mavenCoordinate.url())
          .isEqualTo("https://repo1.maven.org/maven2/group/artifact/1.0.0/artifact-1.0.0.jar");
    }

    @Test
    void with_dotted_groupId() {
      var mavenCoordinate = new MavenCoordinate("com.example.group", "artifact", "1.0.0");
      assertThat(mavenCoordinate.url())
          .isEqualTo(
              "https://repo1.maven.org/maven2/com/example/group/artifact/1.0.0/artifact-1.0.0.jar");
    }
  }

  @Test
  void jar_name() {
    var mavenCoordinate = new MavenCoordinate("group", "artifact", "1.0.0");
    assertThat(mavenCoordinate.jarName()).isEqualTo("artifact-1.0.0.jar");
  }

  @Test
  void null_groupId_throws_exception() {
    assertThrows(
        NullPointerException.class, () -> new MavenCoordinate(null, "artifact", "version"));
  }

  @Test
  void null_artifactId_throws_exception() {
    assertThrows(NullPointerException.class, () -> new MavenCoordinate("group", null, "version"));
  }

  @Test
  void null_version_throws_exception() {
    assertThrows(NullPointerException.class, () -> new MavenCoordinate("group", "artifact", null));
  }
}
