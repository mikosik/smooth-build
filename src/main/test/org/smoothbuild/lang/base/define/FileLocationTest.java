package org.smoothbuild.lang.base.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.define.Space.STANDARD_LIBRARY;
import static org.smoothbuild.lang.base.define.Space.USER;
import static org.smoothbuild.util.Lists.list;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

public class FileLocationTest {
  @Test
  public void equals_and_hash_code() {
    String file = "abc.smooth";
    SModule slibModule = new SModule(STANDARD_LIBRARY, Path.of(file), list());
    SModule userModule1 = new SModule(USER, Path.of(file), list());
    SModule userModule1WithReferences = new SModule(USER, Path.of(file), list(slibModule));
    SModule userModule2 = new SModule(USER, Path.of("def"), list());

    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(
        userModule1.smoothFile(),
        userModule1.smoothFile());
    tester.addEqualityGroup(
        userModule1WithReferences.smoothFile(),
        userModule1WithReferences.smoothFile());
    tester.addEqualityGroup(
        userModule1.nativeFile(),
        userModule1.nativeFile());
    tester.addEqualityGroup(
        userModule2.smoothFile(),
        userModule2.smoothFile());
    tester.addEqualityGroup(
        slibModule.smoothFile(),
        slibModule.smoothFile());

    tester.testEquals();
  }

  @Test
  void prefixed_path() {
    FileLocation moduleLocation =
        new SModule(USER, Path.of("full/path.smooth"), list()).smoothFile();
    assertThat((Object) moduleLocation.prefixedPath())
        .isEqualTo("{prj}/full/path.smooth");
  }

  @Test
  public void to_string() {
    FileLocation location = new SModule(USER, Path.of("abc"), list()).smoothFile();
    assertThat(location.toString())
        .isEqualTo("{prj}/abc");
  }
}
