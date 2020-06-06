package org.smoothbuild;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.lang.base.Space.USER;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.ModulePath;

import com.google.common.testing.EqualsTester;

public class ModulePathTest {
  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(unknownLocation(), unknownLocation());
    tester.addEqualityGroup(modulePath("abc", "def"), modulePath("abc", "def"));
    tester.addEqualityGroup(modulePath("abc", "222"), modulePath("abc", "222"));
    tester.addEqualityGroup(modulePath("111", "def"), modulePath("111", "def"));
    tester.addEqualityGroup(modulePath("111", "222"), modulePath("111", "222"));

    tester.testEquals();
  }

  @Test
  void name() {
    ModulePath modulePath = modulePath("full/module.smooth", "shortPath");
    assertThat(modulePath.name())
        .isEqualTo("module");
  }

  @Test
  void smooth_path() {
    ModulePath modulePath = modulePath("full/path.smooth", "shortPath");
    assertThat((Object) modulePath.smooth().path())
        .isEqualTo(Path.of("full/path.smooth"));
  }

  @Test
  void smooth_shorted() {
    ModulePath modulePath = modulePath("full/path.smooth", "shortPath");
    assertThat((Object) modulePath.smooth().shorted())
        .isEqualTo("shortPath");
  }

  @Test
  void native_path() {
    ModulePath modulePath = modulePath("full/path.smooth", "shortPath");
    assertThat((Object) modulePath.nativ().path())
        .isEqualTo(Path.of("full/path.jar"));
  }

  @Test
  void native_shorted() {
    ModulePath modulePath = modulePath("full/path.smooth", "shortPath");
    assertThat((Object) modulePath.nativ().shorted())
        .isEqualTo("shortPath.jar");
  }

  @Test
  public void file_code_location_to_string() {
    ModulePath location = ModulePath.modulePath(USER, Path.of("abc"), "shortPath");
    assertThat(location.toString())
        .isEqualTo("shortPath(abc)");
  }

  private static ModulePath modulePath(String fullPath, String shortPath) {
    return ModulePath.modulePath(USER, Path.of(fullPath), shortPath);
  }
}
