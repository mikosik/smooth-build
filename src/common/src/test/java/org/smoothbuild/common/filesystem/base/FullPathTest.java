package org.smoothbuild.common.filesystem.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.filesystem.base.Path.path;
import static org.smoothbuild.common.testing.TestingSpace.space;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;

public class FullPathTest {
  @Test
  public void equals_and_hash_code() {
    String file = "abc.smooth";

    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(
        new FullPath(space("project"), path(file)), new FullPath(space("project"), path(file)));
    tester.addEqualityGroup(
        new FullPath(space("project"), path("def")), new FullPath(space("project"), path("def")));
    tester.addEqualityGroup(
        new FullPath(space("sl"), path(file)), new FullPath(space("sl"), path(file)));

    tester.testEquals();
  }

  @Test
  void prefixed_path() {
    var fullPath = new FullPath(space("project"), path("full/path.smooth"));
    assertThat((Object) fullPath.toString()).isEqualTo("{project}/full/path.smooth");
  }

  @Test
  void with_extension() {
    var fullPath = new FullPath(space("project"), path("full/path.smooth"));
    assertThat(fullPath.withExtension("jar"))
        .isEqualTo(new FullPath(space("project"), path("full/path.jar")));
  }

  @Test
  public void to_string() {
    var fullPath = new FullPath(space("project"), path("abc"));
    assertThat(fullPath.toString()).isEqualTo("{project}/abc");
  }
}
