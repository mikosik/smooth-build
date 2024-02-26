package org.smoothbuild.common.filesystem.space;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.filesystem.base.PathS.path;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;

public class FilePathTest {
  @Test
  public void equals_and_hash_code() {
    String file = "abc.smooth";

    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(
        new FilePath(space("project"), path(file)), new FilePath(space("project"), path(file)));
    tester.addEqualityGroup(
        new FilePath(space("project"), path("def")), new FilePath(space("project"), path("def")));
    tester.addEqualityGroup(
        new FilePath(space("sl"), path(file)), new FilePath(space("sl"), path(file)));

    tester.testEquals();
  }

  @Test
  void prefixed_path() {
    var filePath = new FilePath(space("project"), path("full/path.smooth"));
    assertThat((Object) filePath.toString()).isEqualTo("{project}/full/path.smooth");
  }

  @Test
  void with_extension() {
    var filePath = new FilePath(space("project"), path("full/path.smooth"));
    assertThat(filePath.withExtension("jar"))
        .isEqualTo(new FilePath(space("project"), path("full/path.jar")));
  }

  @Test
  public void to_string() {
    var filePath = new FilePath(space("project"), path("abc"));
    assertThat(filePath.toString()).isEqualTo("{project}/abc");
  }

  private static Space space(String name) {
    return new MySpace(name);
  }
}
