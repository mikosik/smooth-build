package org.smoothbuild.util.fs.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.util.fs.space.FilePath;
import org.smoothbuild.util.fs.space.Space;

import com.google.common.testing.EqualsTester;

public class FilePathTest {
  @Test
  public void equals_and_hash_code() {
    String file = "abc.smooth";

    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(
        new FilePath(Space.PRJ, PathS.path(file)),
        new FilePath(Space.PRJ, PathS.path(file)));
    tester.addEqualityGroup(
        new FilePath(Space.PRJ, PathS.path("def")),
        new FilePath(Space.PRJ, PathS.path("def")));
    tester.addEqualityGroup(
        new FilePath(Space.STD_LIB, PathS.path(file)),
        new FilePath(Space.STD_LIB, PathS.path(file)));

    tester.testEquals();
  }

  @Test
  void prefixed_path() {
    FilePath filePath = new FilePath(Space.PRJ, PathS.path("full/path.smooth"));
    assertThat((Object) filePath.toString())
        .isEqualTo("{prj}/full/path.smooth");
  }

  @Test
  void with_extension() {
    FilePath filePath = new FilePath(Space.PRJ, PathS.path("full/path.smooth"));
    assertThat(filePath.withExtension("jar"))
        .isEqualTo(new FilePath(Space.PRJ, PathS.path("full/path.jar")));
  }

  @Test
  public void to_string() {
    FilePath loc = new FilePath(Space.PRJ, PathS.path("abc"));
    assertThat(loc.toString())
        .isEqualTo("{prj}/abc");
  }
}
