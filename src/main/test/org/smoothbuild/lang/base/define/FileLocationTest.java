package org.smoothbuild.lang.base.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.define.Space.PRJ;
import static org.smoothbuild.lang.base.define.Space.SDK;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

public class FileLocationTest {
  @Test
  public void equals_and_hash_code() {
    String file = "abc.smooth";

    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(
        new FileLocation(PRJ, Path.of(file)),
        new FileLocation(PRJ, Path.of(file)));
    tester.addEqualityGroup(
        new FileLocation(PRJ, Path.of("def")),
        new FileLocation(PRJ, Path.of("def")));
    tester.addEqualityGroup(
        new FileLocation(SDK, Path.of(file)),
        new FileLocation(SDK, Path.of(file)));

    tester.testEquals();
  }

  @Test
  void prefixed_path() {
    FileLocation fileLocation = new FileLocation(PRJ, Path.of("full/path.smooth"));
    assertThat((Object) fileLocation.prefixedPath())
        .isEqualTo("{prj}/full/path.smooth");
  }

  @Test
  void with_extension() {
    FileLocation fileLocation = new FileLocation(PRJ, Path.of("full/path.smooth"));
    assertThat(fileLocation.withExtension("jar"))
        .isEqualTo(new FileLocation(PRJ, Path.of("full/path.jar")));
  }

  @Test
  public void to_string() {
    FileLocation location = new FileLocation(PRJ, Path.of("abc"));
    assertThat(location.toString())
        .isEqualTo("{prj}/abc");
  }
}
