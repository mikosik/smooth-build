package org.smoothbuild.lang.base.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.define.Space.STANDARD_LIBRARY;
import static org.smoothbuild.lang.base.define.Space.USER;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

public class FileLocationTest {
  @Test
  public void equals_and_hash_code() {
    String file = "abc.smooth";

    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(
        new FileLocation(USER, Path.of(file)),
        new FileLocation(USER, Path.of(file)));
    tester.addEqualityGroup(
        new FileLocation(USER, Path.of("def")),
        new FileLocation(USER, Path.of("def")));
    tester.addEqualityGroup(
        new FileLocation(STANDARD_LIBRARY, Path.of(file)),
        new FileLocation(STANDARD_LIBRARY, Path.of(file)));

    tester.testEquals();
  }

  @Test
  void prefixed_path() {
    FileLocation fileLocation = new FileLocation(USER, Path.of("full/path.smooth"));
    assertThat((Object) fileLocation.prefixedPath())
        .isEqualTo("{prj}/full/path.smooth");
  }

  @Test
  void with_extension() {
    FileLocation fileLocation = new FileLocation(USER, Path.of("full/path.smooth"));
    assertThat(fileLocation.withExtension("jar"))
        .isEqualTo(new FileLocation(USER, Path.of("full/path.jar")));
  }

  @Test
  public void to_string() {
    FileLocation location = new FileLocation(USER, Path.of("abc"));
    assertThat(location.toString())
        .isEqualTo("{prj}/abc");
  }
}
