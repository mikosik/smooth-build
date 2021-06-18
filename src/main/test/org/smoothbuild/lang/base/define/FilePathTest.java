package org.smoothbuild.lang.base.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.base.define.Space.PRJ;
import static org.smoothbuild.lang.base.define.Space.SDK;

import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

public class FilePathTest {
  @Test
  public void equals_and_hash_code() {
    String file = "abc.smooth";

    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(
        new FilePath(PRJ, path(file)),
        new FilePath(PRJ, path(file)));
    tester.addEqualityGroup(
        new FilePath(PRJ, path("def")),
        new FilePath(PRJ, path("def")));
    tester.addEqualityGroup(
        new FilePath(SDK, path(file)),
        new FilePath(SDK, path(file)));

    tester.testEquals();
  }

  @Test
  void prefixed_path() {
    FilePath filePath = new FilePath(PRJ, path("full/path.smooth"));
    assertThat((Object) filePath.prefixedPath())
        .isEqualTo("{prj}/full/path.smooth");
  }

  @Test
  void with_extension() {
    FilePath filePath = new FilePath(PRJ, path("full/path.smooth"));
    assertThat(filePath.withExtension("jar"))
        .isEqualTo(new FilePath(PRJ, path("full/path.jar")));
  }

  @Test
  public void to_string() {
    FilePath location = new FilePath(PRJ, path("abc"));
    assertThat(location.toString())
        .isEqualTo("{prj}/abc");
  }
}
