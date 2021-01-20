package org.smoothbuild;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.define.Location.internal;
import static org.smoothbuild.lang.base.define.ModuleLocation.moduleLocation;
import static org.smoothbuild.lang.base.define.Space.STANDARD_LIBRARY;
import static org.smoothbuild.lang.base.define.Space.USER;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.define.ModuleLocation;

import com.google.common.testing.EqualsTester;

public class ModuleLocationTest {
  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(
        internal(),
        internal());
    tester.addEqualityGroup(
        moduleLocation(USER, Path.of("abc")),
        moduleLocation(USER, Path.of("abc")));
    tester.addEqualityGroup(
        moduleLocation(USER, Path.of("def")),
        moduleLocation(USER, Path.of("def")));
    tester.addEqualityGroup(
        moduleLocation(STANDARD_LIBRARY, Path.of("abc")),
        moduleLocation(STANDARD_LIBRARY, Path.of("abc")));

    tester.testEquals();
  }

  @Test
  void name() {
    ModuleLocation moduleLocation = moduleLocation(USER, Path.of("full/module.smooth"));
    assertThat(moduleLocation.name())
        .isEqualTo("module");
  }

  @Test
  void prefixed_path() {
    ModuleLocation moduleLocation = moduleLocation(USER, Path.of("full/path.smooth"));
    assertThat((Object) moduleLocation.prefixedPath())
        .isEqualTo("{prj}/full/path.smooth");
  }

  @Test
  public void file_code_location_to_string() {
    ModuleLocation location = moduleLocation(USER, Path.of("abc"));
    assertThat(location.toString())
        .isEqualTo("{prj}/abc");
  }
}
